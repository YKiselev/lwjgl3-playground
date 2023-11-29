/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ykiselev.playground.app.window

import com.github.ykiselev.spi.window.WindowEvents
import org.lwjgl.glfw.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.Callback
import java.util.*

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class WindowBuilder {
    private var fullScreen = false
    private var monitor: Long = 0
    private var width = 0
    private var height = 0
    private var events: WindowEvents = object : WindowEvents {
        override fun keyEvent(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
            return true
        }

        override fun mouseButtonEvent(button: Int, action: Int, mods: Int): Boolean {
            return true
        }

        override fun scrollEvent(dx: Double, dy: Double): Boolean {
            return true
        }
    }

    init {
        GLFW.glfwDefaultWindowHints()
    }

    fun windowed(): WindowBuilder {
        fullScreen = false
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE)
        return this
    }

    fun fullsScreen(): WindowBuilder {
        fullScreen = true
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE)
        return this
    }

    fun fullScreen(fullScreen: Boolean): WindowBuilder {
        return if (fullScreen) fullsScreen() else windowed()
    }

    fun version(major: Int, minor: Int): WindowBuilder {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor)
        return this
    }

    fun anyProfile(): WindowBuilder {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_ANY_PROFILE)
        return this
    }

    fun coreProfile(): WindowBuilder {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        return this
    }

    fun compatibleProfile(): WindowBuilder {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE)
        return this
    }

    fun debug(value: Boolean): WindowBuilder {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, if (value) GL11.GL_TRUE else GL11.GL_FALSE)
        return this
    }

    fun monitor(monitor: Long): WindowBuilder {
        this.monitor = monitor
        return this
    }

    fun primaryMonitor(): WindowBuilder {
        return monitor(GLFW.glfwGetPrimaryMonitor())
    }

    fun dimensions(width: Int, height: Int): WindowBuilder {
        this.width = width
        this.height = height
        return this
    }

    fun events(events: WindowEvents): WindowBuilder {
        this.events = Objects.requireNonNull(events)
        return this
    }

    fun build(title: String): AppWindow {
        val window = GLFW.glfwCreateWindow(
            width,
            height,
            title,
            if (fullScreen) monitor else 0L,
            0
        )
        check(window != 0L) { "Failed to create window" }
        GLFW.glfwMakeContextCurrent(window)
        val capabilities = GL.createCapabilities()
        val debugMessageCallback: Callback? =
            if (GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_OPENGL_DEBUG_CONTEXT) == GLFW.GLFW_TRUE) {
                GLUtil.setupDebugMessageCallback()
            } else null

        val result = AppWindow(window, capabilities, events, debugMessageCallback)
        GLFW.glfwSetFramebufferSizeCallback(
            window,
            GLFWFramebufferSizeCallback.create { wnd, w, h -> result.onFrameBufferSize(wnd, w, h) })
        GLFW.glfwSetWindowSizeCallback(
            window,
            GLFWWindowSizeCallback.create { wnd, w, h -> result.onWindowSize(wnd, w, h) })
        GLFW.glfwSetKeyCallback(window, GLFWKeyCallback.create { wnd, k, sc, a, m -> result.onKey(wnd, k, sc, a, m) })
        GLFW.glfwSetCharCallback(window, GLFWCharCallback.create { wnd, cp -> result.onChar(wnd, cp) })
        GLFW.glfwSetCursorPosCallback(
            window,
            GLFWCursorPosCallback.create { wnd, x, y -> result.onCursorPosition(wnd, x, y) })
        GLFW.glfwSetScrollCallback(window, GLFWScrollCallback.create { wnd, dx, dy -> result.onScroll(wnd, dx, dy) })
        GLFW.glfwSetMouseButtonCallback(
            window,
            GLFWMouseButtonCallback.create { wnd, btn, a, m -> result.onMouseButton(wnd, btn, a, m) })
        GLFW.glfwSetWindowRefreshCallback(window, GLFWWindowRefreshCallback.create { wnd -> result.onRefresh(wnd) })
        return result
    }
}
