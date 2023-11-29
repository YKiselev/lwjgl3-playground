package com.github.ykiselev.playground.services.console

import com.github.ykiselev.common.ThrowingRunnable
import com.github.ykiselev.common.test.ParallelRunner
import com.github.ykiselev.spi.services.commands.CommandException
import com.github.ykiselev.spi.services.commands.Commands
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.util.concurrent.ThreadLocalRandom

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppCommandsTest {

    private val commands: Commands = AppCommands(DefaultTokenizer(), 1)

    @Test
    fun shouldReportUnknownCommand() {
        Assertions.assertThrows(CommandException.UnknownCommandException::class.java) {
            commands.execute("this will not work!")
        }
    }

    @Test
    fun shouldFail() {
        commands.add("fail", Commands.H0 { throw RuntimeException("Oops!") })
        Assertions.assertThrows(CommandException.CommandExecutionFailedException::class.java) {
            commands.execute("fail")
        }
    }

    @Test
    fun shouldOverflow() {
        val h = mock<Commands.H0>()
        commands.add("overflow", h)
        val it = listOf("overflow").iterator()
        doAnswer { _ ->
            if (it.hasNext()) {
                commands.execute(it.next())
            }
            null
        }.`when`(h).handle()
        Assertions.assertThrows(CommandException.CommandStackOverflowException::class.java) { commands.execute("overflow") }
        verify(h, times(1)).handle()
    }

    @Test
    fun shouldExecute() {
        val h = mock<Commands.H0>()
        commands.add("cmd", h)
        commands.execute("cmd")
        verify(h, times(1)).handle()
    }

    @Test
    fun shouldExecuteSeparatedBySemicolon() {
        val h = mock<Commands.H0>()
        commands.add("cmd", h)
        commands.execute("cmd;cmd")
        verify(h, times(2)).handle()
    }

    @Test
    fun shouldExecuteMultiLine() {
        val h = mock<Commands.H0>()
        commands.add("cmd", h)
        commands.execute("cmd\rcmd\ncmd\r\ncmd")
        verify(h, times(4)).handle()
    }

    @Test
    fun shouldPassArgs() {
        val h = mock<Commands.H1>()
        commands.add("cmd", h)
        commands.execute("cmd 1")
        verify(h, times(1)).handle("1")
    }

    @Test
    fun shouldBeThreadSafe() {
        val savedArgs = ThreadLocal<Array<String>>()
        commands.add("a") { a: String, b: String -> savedArgs.set(arrayOf(a, b)) }
        commands.add("b") { a: String, b: String -> savedArgs.set(arrayOf(a, b)) }
        val s = {
            ThrowingRunnable {
                val rnd = ThreadLocalRandom.current()
                val cmd = if (rnd.nextBoolean()) "a" else "b"
                val arg = rnd.nextLong().toString()
                commands.execute("$cmd $arg $arg")
                Assertions.assertArrayEquals(arrayOf(arg, arg), savedArgs.get())
            }
        }
        ParallelRunner.fromRunnable(1000, s)
            .run()
    }
}