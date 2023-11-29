package com.github.ykiselev.playground.init

import org.lwjgl.glfw.GLFW

class GlfwBootstrap : AutoCloseable {
    init {
        GLFW.glfwInit()
    }

    override fun close() {
        GLFW.glfwTerminate()
    }
}
