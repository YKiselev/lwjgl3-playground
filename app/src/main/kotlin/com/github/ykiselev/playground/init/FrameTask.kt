package com.github.ykiselev.playground.init

abstract class FrameTask(private val next: Runnable?) : Runnable {

    protected open fun onFrameStart() {}

    protected open fun onFrameEnd() {}

    override fun run() {
        onFrameStart()
        next?.run()
        onFrameEnd()
    }
}
