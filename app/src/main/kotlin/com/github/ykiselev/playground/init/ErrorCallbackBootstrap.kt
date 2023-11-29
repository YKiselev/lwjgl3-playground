package com.github.ykiselev.playground.init

import com.github.ykiselev.common.closeables.Closeables
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback

class ErrorCallbackBootstrap : AutoCloseable {

    private val callback: GLFWErrorCallback = GLFWErrorCallback.createPrint(System.err)
    private val previous: GLFWErrorCallback? = GLFW.glfwSetErrorCallback(callback)

    override fun close() {
        GLFW.glfwSetErrorCallback(previous)
        Closeables.close(callback)
    }
}