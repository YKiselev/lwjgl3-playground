package com.github.ykiselev.playground.services.console

import com.github.ykiselev.common.circular.ArrayCircularBuffer
import com.github.ykiselev.common.circular.CircularBuffer
import com.github.ykiselev.common.iterators.EndlessIterator
import com.github.ykiselev.common.iterators.MappingIterator
import com.github.ykiselev.opengl.sprites.Colors
import com.github.ykiselev.opengl.sprites.SpriteBatch
import com.github.ykiselev.opengl.sprites.TextAttributes
import com.github.ykiselev.opengl.sprites.TextDrawingFlags
import com.github.ykiselev.opengl.text.Font
import com.github.ykiselev.spi.api.Named
import com.github.ykiselev.spi.services.commands.CommandException
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.configuration.values.ConfigValue
import com.github.ykiselev.spi.services.layers.DrawingContext
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import kotlin.math.sin

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DefaultCommandLine(
    val persistedConfiguration: PersistedConfiguration,
    val commands: Commands,
    historySize: Int,
    private val searchProvider: Function<String, Collection<Named>>
) : CommandLine {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val buf = StringBuilder()
    private val history: CircularBuffer<String> = ArrayCircularBuffer(String::class.java, historySize)
    private val context: Commands.ExecutionContext = object : Commands.ExecutionContext {

        override fun onException(ex: RuntimeException) {
            logger.error(CommandLine.MARKER, ex.toString(), ex)
        }

        override fun onUnknownCommand(args: List<String>) {
            val cfg = persistedConfiguration.root()
            val name = args[0]
            if (cfg.hasVariable(name)) {
                try {
                    val value = cfg.getValue(name, ConfigValue::class.java)
                    if (args.size == 2) {
                        value.setString(args[1])
                    }
                    logger.info(CommandLine.MARKER, "{}=\"{}\"", name, value.toString())
                } catch (ex: RuntimeException) {
                    logger.error(CommandLine.MARKER, ex.toString())
                }
            } else {
                logger.error(CommandLine.MARKER, "Unknown command: {}", name)
            }
        }
    }
    private var cursorPos = 0
    private var historyIndex = 0
    private var historySearch = false
    private var search = false
    private var found = Collections.emptyIterator<String>()

    /**
     * Command fragment copied from `buf` at the start of history search
     */
    private var fragment: String? = null

    /**
     * Adds character at the cursor position.
     *
     *
     * Note: only BMP code points are currently supported.
     *
     * @param codePoint the Unicode code point (UTF-32)
     */
    override fun add(codePoint: Int) {
        buf.insert(cursorPos, codePoint.toChar())
        cursorPos++
        historySearch = false
        search = false
    }

    /**
     * Removes previous character (BackSpace key)
     */
    override fun removeLeft() {
        if (cursorPos > 0 && cursorPos - 1 < buf.length) {
            buf.delete(cursorPos - 1, cursorPos)
            cursorPos--
        }
        historySearch = false
        search = false
    }

    /**
     * Removes character at cursor position (Del key)
     */
    override fun remove() {
        if (cursorPos < buf.length) {
            buf.delete(cursorPos, cursorPos + 1)
        }
        historySearch = false
        search = false
    }

    /**
     * Moves cursor one char left
     */
    override fun left() {
        if (cursorPos > 0) {
            cursorPos--
        }
    }

    /**
     * Moves cursor one char right
     */
    override fun right() {
        if (cursorPos < buf.length) {
            cursorPos++
        }
    }

    override fun draw(ctx: DrawingContext, x0: Int, y0: Int, width: Int, height: Int) {
        val font: Font = ctx.textAttributes.font()
        val y = y0 + font.lineSpace() + 3
        val batch: SpriteBatch = ctx.batch
        val attributes: TextAttributes = ctx.textAttributes
        val restoreUseCcs = attributes.remove(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES)
        batch.draw(x0, y, width, buf, attributes)
        val cursorWidth = font.width("_")
        val brightness = sin(8 * GLFW.glfwGetTime()).toFloat()
        val prevColor = attributes.color()
        attributes.color(Colors.fade(prevColor, brightness))
        val x = x0 + cursorWidth * cursorPos
        batch.draw(x, y, width, "_", attributes)
        batch.draw(x, y + 1, width, "_", attributes)
        attributes.color(prevColor)
        if (restoreUseCcs) {
            attributes.add(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES)
        }
    }

    /**
     * Moves cursor to the beginning of the command line
     */
    override fun begin() {
        cursorPos = 0
    }

    /**
     * Moves the cursor right after the last command line character
     */
    override fun end() {
        cursorPos = buf.length
    }

    /**
     * Tries to complete entered command
     */
    override fun complete() {
        if (!search) {
            if (buf.isEmpty()) {
                return
            }
            search = true
            val names = searchProvider.apply(buf.toString())
            if (names.isEmpty()) {
                found = Collections.emptyIterator()
            } else {
                found = MappingIterator(EndlessIterator(names), Named::name)
                val it = names.iterator()
                if (names.size > 1) {
                    var i = 0
                    while (i < 8 && it.hasNext()) {
                        val v = it.next()
                        if (v is ConfigValue) {
                            logger.info(CommandLine.MARKER, "  {}=^0b\"{}\"", v.name, v)
                        } else {
                            logger.info(CommandLine.MARKER, "  {}", v.name)
                        }
                        i++
                    }
                    if (names.size > 8) {
                        logger.info(CommandLine.MARKER, "...and {} more.", names.size - 8)
                    }
                }
            }
        }
        if (found.hasNext()) {
            set(found.next())
        }
    }

    override fun reset() {
        buf.setLength(0)
        cursorPos = 0
        historySearch = false
        search = false
        fragment = null
        found = Collections.emptyIterator()
    }

    private fun addToHistory(commandLine: String) {
        if (!history.isEmpty) {
            val previous = history[history.count() - 1]
            if (previous == commandLine) {
                return
            }
        }
        history.write(Objects.requireNonNull(commandLine))
    }

    /**
     * Executes current command line (if not empty).
     */
    override fun execute() {
        if (buf.isEmpty()) {
            return
        }
        val commandLine = buf.toString()
        reset()
        try {
            commands.execute(commandLine, context)
            addToHistory(commandLine)
        } catch (ex: CommandException) {
            logger.error(CommandLine.MARKER, ex.message)
        }
    }

    private fun set(commandLine: String) {
        buf.setLength(0)
        buf.append(commandLine)
        buf.append(' ')
        cursorPos = buf.length
    }

    private fun searchHistory(supplier: Supplier<String?>) {
        search = false
        if (!historySearch) {
            historySearch = true
            fragment = buf.toString()
            historyIndex = history.count()
        }
        while (true) {
            val cmd = supplier.get() ?: return
            if (cmd.isNotEmpty() && fragment?.isNotEmpty() == true) {
                if (!cmd.startsWith(fragment!!)) {
                    continue
                }
            }
            set(cmd)
            break
        }
    }

    /**
     * @return the previous history command or `null` if oldest command was already returned.
     */
    private fun prevHistory(): String? {
        if (historyIndex > history.count()) {
            historyIndex = history.count()
        }
        return if (historyIndex > 0) {
            history[--historyIndex]
        } else null
    }

    /**
     * @return the next history command or empty string if latest command was already returned.
     */
    private fun nextHistory(): String {
        if (historyIndex < 0) {
            historyIndex = -1
        }
        return if (historyIndex + 1 < history.count()) {
            history[++historyIndex]
        } else ""
    }

    override fun searchHistoryBackward() {
        searchHistory { prevHistory() }
    }

    override fun searchHistory() {
        searchHistory { nextHistory() }
    }
}