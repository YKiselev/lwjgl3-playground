package com.github.ykiselev.playground.init

import com.github.ykiselev.opengl.sprites.SpriteBatch
import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.services.layers.UiLayers

class UiLayersTask(next: Runnable?, private val uiLayers: UiLayers, spriteBatch: SpriteBatch) : FrameTask(next) {

    private val context = DrawingContext(spriteBatch)

    override fun onFrameStart() {
        uiLayers.draw(context)
    }

    override fun onFrameEnd() {
        // no-op
    }
}
