package com.github.ykiselev.base.game.client

import com.github.ykiselev.base.game.Pyramids
import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.common.fps.FrameInfo
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.buffers.FrameBuffer
import com.github.ykiselev.opengl.fonts.TrueTypeFont
import com.github.ykiselev.opengl.matrices.Matrix
import com.github.ykiselev.opengl.matrices.identity
import com.github.ykiselev.opengl.sprites.Colors
import com.github.ykiselev.opengl.sprites.TextAlignment
import com.github.ykiselev.opengl.sprites.TextAttributes
import com.github.ykiselev.opengl.textures.Texture2d
import com.github.ykiselev.opengl.textures.writeCurrentTexture2d
import com.github.ykiselev.spi.GameFactoryArgs
import com.github.ykiselev.spi.api.Updatable
import com.github.ykiselev.spi.services.FileSystem
import com.github.ykiselev.spi.services.commands.Commands
import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.window.Window
import com.github.ykiselev.spi.window.WindowEvents
import com.github.ykiselev.spi.world.World
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.FloatBuffer
import java.util.*

class GameClient(host: GameFactoryArgs) : Updatable, AutoCloseable, WindowEvents {
    internal enum class FrameBufferMode {
        COLOR,
        DEPTH,
        NORMAL
    }

    private val logger = LoggerFactory.getLogger(javaClass)
    private val commands: Commands
    private val window: Window
    private val frameInfo: FrameInfo
    private val fileSystem: FileSystem
    private val cuddles: Texture2d
    private val ttf: TrueTypeFont
    private val vp: Matrix = Matrix()
    private var lmbPressed = false
    private var rmbPressed = false
    private var active = false
    private var mx = 0.0
    private var my = 0.0
    private var dx = 0.0
    private var dy = 0.0
    private val frameBuffer: FrameBuffer
    private val pyramids: Pyramids
    private val renderer: WorldRenderer
    private val closeable: AutoCloseable
    private val camera = Camera()
    private var frameBufferMode = FrameBufferMode.COLOR

    init {
        commands = Objects.requireNonNull(host.commands)
        fileSystem = Objects.requireNonNull(host.fileSystem)
        frameInfo = Objects.requireNonNull(host.frameInfo)
        window = Objects.requireNonNull(host.window)
        val assets = host.assets
        Closeables.newGuard().use { guard ->
            val tex = assets.load("images/console.jpg", OglRecipes.SPRITE)
            guard.add(tex)
            cuddles = tex.value()
            val atlas = assets.load("font-atlases/base.conf", OglRecipes.FONT_ATLAS)
            guard.add(atlas)
            ttf = atlas.value()["console"]!!
            pyramids = guard.add(Pyramids(assets))
            renderer = guard.add(WorldRenderer(assets))
            //vp = MemoryUtil.memAllocFloat(16)
            frameBuffer = guard.add(FrameBuffer())
            closeable = guard.detach()
        }
        camera.set(0f, -10f, 0f)
    }

    override fun update() {}

    @Throws(Exception::class)
    override fun close() {
        //MemoryUtil.memFree(vp)
        closeable.close()
    }

    private fun setupProjectionViewMatrix(width: Int, height: Int) {
        identity(vp)
        camera.apply(width.toFloat() / height, vp)
    }

    override fun keyEvent(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
            when (key) {
                GLFW.GLFW_KEY_ESCAPE -> commands.execute("show-menu")
                GLFW.GLFW_KEY_GRAVE_ACCENT -> commands.execute("toggle-console")
                GLFW.GLFW_KEY_PRINT_SCREEN -> {
                    try {
                        dumpToFile(frameBuffer.color, "color.png")
                        dumpToFile(frameBuffer.depth, "depth.png")
                        dumpToFile(frameBuffer.normal, "normal.png")
                    } catch (e: IOException) {
                        logger.error("Unable to save image!", e)
                    }
                }

                GLFW.GLFW_KEY_F1 -> frameBufferMode = when (frameBufferMode) {
                    FrameBufferMode.COLOR -> FrameBufferMode.DEPTH
                    FrameBufferMode.DEPTH -> FrameBufferMode.NORMAL
                    FrameBufferMode.NORMAL -> FrameBufferMode.COLOR
                }

                GLFW.GLFW_KEY_W -> camera.move(0.5f)
                GLFW.GLFW_KEY_S -> camera.move(-0.5f)
                GLFW.GLFW_KEY_A -> camera.strafe(-0.5f)
                GLFW.GLFW_KEY_D -> camera.strafe(0.5f)
                GLFW.GLFW_KEY_SPACE -> camera.moveUp(0.5f)
                GLFW.GLFW_KEY_C -> camera.moveUp(-0.5f)
            }
        }
        return true
    }

    @Throws(IOException::class)
    private fun dumpToFile(texture: Texture2d, name: String) {
        texture.bind()
        try {
            fileSystem.truncate(name).use { channel ->
                writeCurrentTexture2d {
                    channel.write(it)
                }
            }
        } finally {
            texture.unbind()
        }
    }

    override fun cursorEvent(x: Double, y: Double) {
        if (!active) {
            return
        }
        // Skip first move for uninitialized mx, my to avoid sudden camera rotation
        if (mx == 0.0 && my == 0.0) {
            mx = x
            my = y
            return
        }
        dx = x - mx
        dy = y - my
        mx = x
        my = y
        camera.rotate(dx, dy)
    }

    override fun mouseButtonEvent(button: Int, action: Int, mods: Int): Boolean {
        if (!active) {
            return false
        }
        when (button) {
            GLFW.GLFW_MOUSE_BUTTON_LEFT -> lmbPressed = action == GLFW.GLFW_PRESS
            GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                rmbPressed = action == GLFW.GLFW_PRESS
                //rmbTrigger.value(action == GLFW.GLFW_PRESS);
            }
        }
        return true
    }

    override fun frameBufferResized(width: Int, height: Int) {
        // no-op
    }

    override fun scrollEvent(dx: Double, dy: Double): Boolean {
        return true
    }

    fun draw(width: Int, height: Int, context: DrawingContext, world: World?) {
        frameBuffer.size(width, height)
        GL11.glViewport(0, 0, width, height)
        GL11.glFrontFace(GL11.GL_CCW)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glDisable(GL11.GL_STENCIL_TEST)
        GL11.glDepthFunc(GL11.GL_LESS)
        GL11.glDepthMask(true)
        setupProjectionViewMatrix(width, height)
        frameBuffer.bind()
        GL11.glClearDepth(100.0)
        GL11.glClearColor(0f, 0f, 0.5f, 1f)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        //GL20.glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1});
        pyramids.draw(vp)
        renderer.draw(world, vp)

        //drawModel(vp);
        frameBuffer.unbind()
        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        val textAttributes: TextAttributes = context.textAttributes
        textAttributes.trueTypeFont(ttf)
        textAttributes.alignment(TextAlignment.LEFT)
        textAttributes.color(Colors.WHITE)
        val spriteBatch = context.batch
        spriteBatch.begin(0, 0, width, height, true)
        when (frameBufferMode) {
            FrameBufferMode.COLOR -> spriteBatch.draw(frameBuffer.color, 0, 0, width, height, 0f, 0f, 1f, 1f, -0x1)
            FrameBufferMode.DEPTH -> spriteBatch.draw(frameBuffer.depth, 0, 0, width, height, 0f, 0f, 1f, 1f, -0x1)
            FrameBufferMode.NORMAL -> spriteBatch.draw(
                frameBuffer.normal,
                0,
                0,
                width,
                height,
                0f,
                0f,
                1f,
                1f,
                -0x1
            )
        }
        spriteBatch.draw(
            0, height, width, String.format(
                "time (ms): min: %.1f, max: %.1f, avg: %.1f, fps: %.2f, frame buffer mode: %s, %s",
                frameInfo.min(), frameInfo.max(), frameInfo.avg(), frameInfo.fps(), frameBufferMode,
                renderer.formatStats()
            ),
            textAttributes
        )

        // debug
        textAttributes.color(Colors.rgb(255, 255, 0))
        textAttributes.spriteFont(null)
        textAttributes.trueTypeFont(ttf)

        //spriteBatch.draw(10, height - 30, width, "This is the test! 0123456789.\nSecond line of text ~?!:#@$%^&*()_+", textAttributes);
        spriteBatch.end()
    }

    fun activate(active: Boolean) {
        my = 0.0
        mx = my
        this.active = active
    }
}
