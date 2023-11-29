package com.github.ykiselev.playground.init

import com.github.ykiselev.common.fps.FrameInfo
import org.lwjgl.glfw.GLFW

class FrameInfoTracker(next: Runnable?, private val frameInfo: FrameInfo) : FrameTask(next) {

    private var t0 = 0.0

    override fun onFrameStart() {
        t0 = GLFW.glfwGetTime()
    }

    override fun onFrameEnd() {
        val t1 = GLFW.glfwGetTime()
        frameInfo.add((t1 - t0) * 1000.0)
    }
}
