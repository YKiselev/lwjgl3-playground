package com.github.ykiselev.playground.services.console

import com.github.ykiselev.common.cow.CopyOnModify
import com.github.ykiselev.spi.services.commands.Command
import com.github.ykiselev.spi.services.commands.CommandException
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.commands.Tokenizer
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Stream

/**
 * @param tokenizer  the tokenizer to use.
 * @param maxDepth the maximum depth of recursive calls.
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppCommands(private val tokenizer: Tokenizer, private val maxDepth: Int = 16) : Commands, AutoCloseable {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val handlers = CopyOnModify(emptyMap<String?, Command>())
    private val threadContext: ThreadLocal<ThreadContext> = ThreadLocal.withInitial { ThreadContext(maxDepth) }

    override fun close() {
        // no-op for now
    }

    override fun execute(commandLine: String, context: Commands.ExecutionContext) {
        if (commandLine.isEmpty()) {
            return
        }
        val tc = threadContext.get()
        tc.enter(context)
        try {
            val args = tc.args
            args.clear()
            var fromIndex = 0
            while (fromIndex < commandLine.length) {
                fromIndex = execute(commandLine, fromIndex, args, tc.context())
                args.clear()
            }
        } finally {
            tc.leave()
        }
    }

    private fun handler(command: String?): Command? {
        return handlers.value()[command]
    }

    private fun execute(
        commandLine: String,
        fromIndex: Int,
        args: MutableList<String>,
        ctx: Commands.ExecutionContext
    ): Int {
        val result = tokenizer.tokenize(commandLine, fromIndex, args)
        if (args.isNotEmpty()) {
            val handler = handler(args[0])
            if (handler != null) {
                try {
                    handler.run(args)
                } catch (e: CommandException.CommandStackOverflowException) {
                    ctx.onException(e)
                } catch (e: CommandException.CommandExecutionFailedException) {
                    ctx.onException(e)
                } catch (e: Exception) {
                    ctx.onException(CommandException.CommandExecutionFailedException(commandLine, e))
                }
            } else {
                ctx.onUnknownCommand(args)
            }
        }
        return result
    }

    override fun commands(): Stream<Command> {
        return handlers.value()
            .values
            .stream()
    }

    @Throws(CommandException.CommandAlreadyRegisteredException::class)
    override fun add(handler: Command): AutoCloseable {
        handlers.modify { before: Map<String?, Command>? ->
            val after: MutableMap<String?, Command> = HashMap(before)
            if (after.putIfAbsent(handler.name, handler) != null) {
                throw CommandException.CommandAlreadyRegisteredException(handler.name)
            }
            after
        }
        return AutoCloseable { remove(handler.name) }
    }

    private fun remove(command: String) {
        handlers.modify { before: Map<String?, Command>? ->
            val after: MutableMap<String?, Command> = HashMap(before)
            if (after.remove(command) == null) {
                logger.warn("Unable to remove (not found): {}", command)
            }
            after
        }
    }

    private class ThreadContext(private val maxDepth: Int) {

        val args: MutableList<String> = mutableListOf()
        private val queue: Queue<Commands.ExecutionContext> = ArrayDeque(maxDepth)

        fun context(): Commands.ExecutionContext =
            queue.element()

        fun enter(context: Commands.ExecutionContext) {
            if (queue.size >= maxDepth) {
                throw CommandException.CommandStackOverflowException(maxDepth)
            }
            queue.add(context)
        }

        fun leave() {
            queue.remove()
        }
    }
}