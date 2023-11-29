package com.github.ykiselev.playground.init

import com.github.ykiselev.playground.app.window.AppWindow

class WindowTask(next: Runnable?, private val window: AppWindow) : FrameTask(next) {

    override fun onFrameStart() {
        window.makeCurrent()
        window.checkEvents()
    }

    override fun onFrameEnd() {
        window.swapBuffers()
    }
}
