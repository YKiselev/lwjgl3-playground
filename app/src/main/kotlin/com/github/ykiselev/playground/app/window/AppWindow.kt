package com.github.ykiselev.playground.app.window

import com.github.ykiselev.spi.window.Window
import com.github.ykiselev.spi.window.WindowEvents
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer

/**
 * Window class.
 * Creates window and sets keyboard, mouse and frame buffer resize callbacks
 */
class AppWindow(
    private val window: Long,
    private val capabilities: GLCapabilities,
    private val windowEvents: WindowEvents,
    private val debugMessageCallback: Callback?
) : AutoCloseable, Window {

    /**
     * Force frame buffer resize on window creation
     */
    private var frameBufferResized = true
    private var windowResized = false

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onFrameBufferSize(window: Long, width: Int, height: Int) {
        frameBufferResized = true
    }

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onWindowSize(window: Long, width: Int, height: Int) {
        windowResized = true
    }

    /**
     * Event handlers left package-private to be accessible from [WindowBuilder] class.
     */
    fun onKey(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) {
        windowEvents.keyEvent(key, scanCode, action, mods)
    }

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onChar(window: Long, codePoint: Int) {
        windowEvents.charEvent(codePoint)
    }

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onCursorPosition(window: Long, x: Double, y: Double) {
        windowEvents.cursorEvent(x, y)
    }

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onMouseButton(window: Long, button: Int, action: Int, mods: Int) {
        windowEvents.mouseButtonEvent(button, action, mods)
    }

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onScroll(window: Long, dx: Double, dy: Double) {
        windowEvents.scrollEvent(dx, dy)
    }

    /**
     * Event handlers left package-private to be accessible form [WindowBuilder] class.
     */
    fun onRefresh(window: Long) {}
    override fun close() {
        GL.setCapabilities(null)
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)
        debugMessageCallback?.close()
    }

    fun show() {
        GLFW.glfwShowWindow(window)
    }

    fun hide() {
        GLFW.glfwHideWindow(window)
    }

    fun shouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(window)
    }

    fun makeCurrent() {
        GLFW.glfwMakeContextCurrent(window)
    }

    fun checkEvents() {
        checkForFrameBufferResize()
        checkWindowResize()
        GLFW.glfwPollEvents()
    }

    private fun checkWindowResize() {
        if (windowResized) {
            windowResized = false
        }
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(window)
    }

    private fun checkForFrameBufferResize() {
        if (frameBufferResized) {
            val width: Int
            val height: Int
            MemoryStack.stackPush().use { ms ->
                val wb = ms.callocInt(1)
                val hb = ms.callocInt(1)
                GLFW.glfwGetFramebufferSize(window, wb, hb)
                width = wb[0]
                height = hb[0]
            }
            frameBufferResized = false
            windowEvents.frameBufferResized(width, height)
        }
    }

    override fun getContentScale(xScale: FloatBuffer, yScale: FloatBuffer) {
        GLFW.glfwGetWindowContentScale(window, xScale, yScale)
    }

    override fun setCursorPos(xpos: Double, ypos: Double) {
        GLFW.glfwSetCursorPos(window, xpos, ypos)
    }

    override fun showCursor() {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
    }

    override fun hideCursor() {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
    }
}