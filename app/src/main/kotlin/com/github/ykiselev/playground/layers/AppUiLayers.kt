package com.github.ykiselev.playground.layers

import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.services.layers.UiLayer
import com.github.ykiselev.spi.services.layers.UiLayers
import com.github.ykiselev.spi.window.WindowEvents

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppUiLayers : UiLayers, AutoCloseable {

    private val layers: MutableList<UiLayer> = mutableListOf()
    private val events: WindowEvents = object : WindowEvents {
        override fun keyEvent(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
            for (i in layers.indices.reversed()) {
                if (layers[i].events().keyEvent(key, scanCode, action, mods)) {
                    return true
                }
            }
            return false
        }

        override fun charEvent(codePoint: Int): Boolean {
            for (i in layers.indices.reversed()) {
                if (layers[i].events().charEvent(codePoint)) {
                    return true
                }
            }
            return false
        }

        /**
         * Note: [SpriteBatch] uses left lower corner as a coordinate system origin (0,0)
         * while GLFW passes cursor coordinates relative to upper-left corner of the window, so here we translate `y` coordinate
         * to be 0 at the bottom of a window and `height` at the top.
         *
         * @param x the x coordinate (relative to window left)
         * @param y the y coordinate (relative to window top)
         */
        override fun cursorEvent(x: Double, y: Double) {
            for (i in layers.indices.reversed()) {
                layers[i].events().cursorEvent(x, height - y)
            }
        }

        override fun mouseButtonEvent(button: Int, action: Int, mods: Int): Boolean {
            for (i in layers.indices.reversed()) {
                if (layers[i].events().mouseButtonEvent(button, action, mods)) {
                    return true
                }
            }
            return false
        }

        override fun frameBufferResized(width: Int, height: Int) {
            resize(width, height)
            for (i in layers.indices) {
                layers[i].events().frameBufferResized(width, height)
            }
        }

        override fun scrollEvent(dx: Double, dy: Double): Boolean {
            for (i in layers.indices.reversed()) {
                if (layers[i].events().scrollEvent(dx, dy)) {
                    return true
                }
            }
            return false
        }
    }

    private var width = 0
    private var height = 0

    private fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun events(): WindowEvents {
        return events
    }

    override fun draw(context: DrawingContext) {
        for (i in layers.indices) {
            layers[i].draw(width, height, context)
        }
    }

    private fun fireOnActivation() {
        for (i in layers.indices) {
            layers[i].onActivation(i == layers.size - 1)
        }
    }

    override fun pop(layer: UiLayer) {
        val last = layers.size - 1
        require(layers[last] === layer) { "Not a top layer: $layer" }
        layers.removeAt(last).onPop()
        fireOnActivation()
    }

    override fun removePopups() {
        for (i in layers.indices.reversed()) {
            if (!layers[i].isPopup) {
                break
            }
            layers.removeAt(i).onPop()
        }
        fireOnActivation()
    }

    override fun add(layer: UiLayer) {
        if (layers.contains(layer)) {
            return
        }
        layers.add(layer)
        // Note: sort is expected to be stable!
        layers.sortWith(LAYER_COMPARATOR)
        layer.onPush()
        fireOnActivation()
    }

    override fun remove(layer: UiLayer) {
        if (layers.remove(layer)) {
            layer.onPop()
        }
        fireOnActivation()
    }

    override fun close() {
        // no-op for now
    }

    companion object {
        private val LAYER_COMPARATOR = Comparator.comparingInt { layer: UiLayer -> layer.kind().ordinal }
    }
}