package com.github.ykiselev.playground.init

import com.github.ykiselev.playground.services.GameBootstrap

class GameTask(next: Runnable?, private val game: GameBootstrap) : FrameTask(next) {
    override fun onFrameStart() {
        game.update()
    }
}
