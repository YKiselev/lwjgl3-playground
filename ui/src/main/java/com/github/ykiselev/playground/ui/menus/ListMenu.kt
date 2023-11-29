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
package com.github.ykiselev.playground.ui.menus

import com.github.ykiselev.opengl.sprites.Colors
import com.github.ykiselev.opengl.sprites.SpriteBatch
import com.github.ykiselev.opengl.sprites.TextAlignment
import com.github.ykiselev.opengl.sprites.TextAttributes
import com.github.ykiselev.playground.ui.UiElement
import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.services.layers.UiLayer
import com.github.ykiselev.spi.window.WindowEvents
import org.lwjgl.glfw.GLFW
import kotlin.math.max

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ListMenu(val items: List<MenuItem>) : UiLayer {

    data class MenuItem(val title: String = "", val element: UiElement)

    constructor(vararg items: MenuItem) : this(items.toList())

    private val events: WindowEvents = object : WindowEvents {
        override fun keyEvent(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
            if (selectedItem().keyEvent(key, scanCode, action, mods)) {
                return true
            }
            if (action == GLFW.GLFW_PRESS) {
                when (key) {
                    GLFW.GLFW_KEY_UP -> {
                        selectPrevious()
                        return true
                    }

                    GLFW.GLFW_KEY_DOWN -> {
                        selectNext()
                        return true
                    }
                }
            }
            return false
        }

        override fun cursorEvent(x: Double, y: Double) {
            for (item in items) {
                item.element.cursorEvent(x, y)
            }
        }

        override fun mouseButtonEvent(button: Int, action: Int, mods: Int): Boolean {
            if (!selectedItem().mouseButtonEvent(button, action, mods)) {
                // todo
            }
            return true
        }

        override fun frameBufferResized(width: Int, height: Int) {
            items.forEach {
                it.element.frameBufferResized(width, height)
            }
        }

        override fun scrollEvent(dx: Double, dy: Double): Boolean {
            if (!selectedItem().scrollEvent(dx, dy)) {
                if (dy > 0) {
                    selectPrevious()
                } else if (dy < 0) {
                    selectNext()
                }
            }
            return true
        }
    }

    private var selected = 0

    override fun kind(): UiLayer.Kind =
        UiLayer.Kind.POPUP

    override fun events(): WindowEvents =
        events

    private fun selectedItem(): UiElement =
        items[selected].element

    private fun selectNext() {
        selected++
        if (selected >= items.size) {
            selected = 0
        }
    }

    private fun selectPrevious() {
        selected--
        if (selected < 0) {
            selected = items.size - 1
        }
    }

    override fun draw(width: Int, height: Int, context: DrawingContext) {
        val batch: SpriteBatch = context.batch
        batch.begin(0, 0, width, height, true)
        context.batch.fill(0, 0, width, height, 0x000030df)
        val attributes: TextAttributes = context.textAttributes
        val half = width / 2
        val titleWidth = half - 64
        val font = attributes.trueTypeFont()
        val cursorWidth = font.width(SELECTOR)
        val x = titleWidth + cursorWidth
        val maxWidth = width - x
        var y = height / 2 + items.size * font.height() / 2 - font.height()
        val prevTextAlignment = attributes.alignment()
        for (i in items.indices) {
            val item = items[i]
            val dx = 0
            var th = 0
            if (item.title.isNotEmpty()) {
                attributes.alignment(TextAlignment.RIGHT)
                th = batch.draw(x - (cursorWidth + titleWidth), y, titleWidth, item.title, attributes)
                attributes.alignment(prevTextAlignment)
            }
            if (i == selected) {
                val prevColor = attributes.color()
                val brightness = System.currentTimeMillis() % 255 / 255f
                attributes.color(Colors.fade(prevColor, brightness))
                batch.draw(x - cursorWidth, y, maxWidth, SELECTOR, attributes)
                attributes.color(prevColor)
            }
            val eh = item.element.draw(x + dx, y, maxWidth, context)
            y -= max(th, eh)
        }
        batch.end()
    }

    companion object {
        const val SELECTOR = "\u25BA"
    }
}
