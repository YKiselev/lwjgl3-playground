package com.github.ykiselev.playground.services.console

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.fonts.TrueTypeFont
import com.github.ykiselev.opengl.sprites.SpriteBatch
import com.github.ykiselev.opengl.sprites.TextDrawingFlags
import com.github.ykiselev.opengl.textures.Texture2d
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.services.layers.UiLayer
import com.github.ykiselev.spi.services.layers.UiLayers
import com.github.ykiselev.spi.window.WindowEvents
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.min

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppConsole(
    val commands: Commands,
    configuration: PersistedConfiguration,
    val buffer: ConsoleBuffer,
    val commandLine: CommandLine,
    assets: Assets,
    private val uiLayers: UiLayers
) : UiLayer, AutoCloseable {

    private val logger = LoggerFactory.getLogger(javaClass)
    private var background: Texture2d? = null
    private val events: WindowEvents = object : WindowEvents {

        override fun keyEvent(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
            return onKey(key, scanCode, action, mods)
        }

        override fun charEvent(codePoint: Int): Boolean {
            if (inputAllowed) {
                commandLine.add(codePoint)
            } else if (showing) {
                inputAllowed = true
            }
            return true
        }

        override fun scrollEvent(dx: Double, dy: Double): Boolean {
            if (inputAllowed) {
                // todo
                buffer.scroll(dy.toInt().toDouble())
            }
            return showing
        }
    }
    private val ac: AutoCloseable
    private val ttf: TrueTypeFont
    private var consoleHeight = 0.0
    private var showTime = 3.0
    private var backgroundColor = -0x1
    private var textColor = -0x1
    private var showing = false
    private var inputAllowed = false
    private var prevTime = 0.0

    override fun events(): WindowEvents {
        return events
    }

    init {
        Closeables.newGuard().use { guard ->
            guard.add(
                configuration.wire()
                    .withDouble("console.showTime", { showTime }, { v: Double -> showTime = v }, true)
                    .withHexInt("console.textColor", { textColor }, { v: Int -> textColor = v }, true)
                    .withHexInt("console.backgroundColor", { backgroundColor }, { v: Int -> backgroundColor = v }, true)
                    .build()
            )
            guard.add(
                commands.add()
                    .with("toggle-console", ::onToggleConsole)
                    .with("echo", ::onEcho)
                    .build()
            )
            background = guard.add(assets.load("images/console.jpg", OglRecipes.SPRITE))
            val atlas = guard.add(assets.load("font-atlases/base.conf", OglRecipes.FONT_ATLAS))
            ttf = atlas["console"]!!
            ac = guard.detach()
        }
    }

    private fun onEcho(args: List<String>) {
        logger.info("{}", args)
    }

    private fun onToggleConsole() {
        // Always disable input to filter out characters from toggle key (i.e. '~'). Would be enabled on next draw call (if showing).
        inputAllowed = false
        showing = !showing
        prevTime = GLFW.glfwGetTime()
        if (showing) {
            uiLayers.add(this)
        }
    }

    private fun onKey(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
        if (action != GLFW.GLFW_RELEASE) {
            when (key) {
                GLFW.GLFW_KEY_ESCAPE -> {
                    showing = false
                    consoleHeight = 0.0
                    inputAllowed = false
                    uiLayers.pop(this)
                    commands.execute("show-menu")
                    return true
                }

                GLFW.GLFW_KEY_GRAVE_ACCENT -> {
                    onToggleConsole()
                    return true
                }

                GLFW.GLFW_KEY_LEFT -> {
                    commandLine.left()
                    return true
                }

                GLFW.GLFW_KEY_RIGHT -> {
                    commandLine.right()
                    return true
                }

                GLFW.GLFW_KEY_UP -> {
                    commandLine.searchHistoryBackward()
                    return true
                }

                GLFW.GLFW_KEY_DOWN -> {
                    commandLine.searchHistory()
                    return true
                }

                GLFW.GLFW_KEY_HOME -> {
                    commandLine.begin()
                    return true
                }

                GLFW.GLFW_KEY_END -> {
                    commandLine.end()
                    return true
                }

                GLFW.GLFW_KEY_ENTER -> {
                    commandLine.execute()
                    return true
                }

                GLFW.GLFW_KEY_TAB -> {
                    commandLine.complete()
                    return true
                }

                GLFW.GLFW_KEY_BACKSPACE -> {
                    commandLine.removeLeft()
                    return true
                }

                GLFW.GLFW_KEY_DELETE -> {
                    commandLine.remove()
                    return true
                }

                GLFW.GLFW_KEY_PAGE_UP -> {
                    buffer.pageUp()
                    return true
                }

                GLFW.GLFW_KEY_PAGE_DOWN -> {
                    buffer.pageDown()
                    return true
                }
            }
        }
        return showing
    }

    override fun draw(width: Int, height: Int, context: DrawingContext) {
        calculateHeight(height)
        if (consoleHeight <= 0) {
            uiLayers.remove(this)
            return
        }
        drawConsole(0, height - consoleHeight.toInt(), width, height, context)
    }

    private fun calculateHeight(viewHeight: Int) {
        val t = GLFW.glfwGetTime()
        val deltaTime = t - prevTime
        // well, it's much easier to do with per frame increments than with total toggle delta time,
        // especially when toggle button is pressed again while previous toggle cycle is not yet complete
        prevTime = t
        val deltaHeight = (if (showing) 1 else -1) * viewHeight * deltaTime / showTime
        consoleHeight = max(0.0, min(viewHeight.toDouble(), consoleHeight + deltaHeight))
    }

    private fun drawConsole(x0: Int, y0: Int, width: Int, height: Int, context: DrawingContext) {
        val attributes = context.textAttributes
        attributes.trueTypeFont(ttf)
        attributes.add(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES)
        val spriteBatch: SpriteBatch = context.batch
        spriteBatch.begin(x0, y0, width, consoleHeight.toInt(), true)
        spriteBatch.draw(background, x0, y0, width, height, backgroundColor)
        attributes.color(textColor)
        buffer.draw(context, x0, y0, width, height)
        commandLine.draw(context, x0, y0, width, height)
        spriteBatch.end()
    }

    override fun kind(): UiLayer.Kind {
        return UiLayer.Kind.POPUP
    }

    override fun close() {
        uiLayers.remove(this)
        Closeables.close(ac)
    }
}