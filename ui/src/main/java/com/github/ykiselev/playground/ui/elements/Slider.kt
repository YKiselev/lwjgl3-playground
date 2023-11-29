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
package com.github.ykiselev.playground.ui.elements

import com.github.ykiselev.playground.ui.AbstractUiElement
import com.github.ykiselev.playground.ui.models.slider.SliderModel
import com.github.ykiselev.spi.services.layers.DrawingContext
import org.lwjgl.glfw.GLFW

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Slider(private val model: SliderModel) : AbstractUiElement() {

    override fun onKey(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
        if (action == GLFW.GLFW_PRESS) {
            when (key) {
                GLFW.GLFW_KEY_LEFT -> {
                    model.decrease()
                    return true
                }

                GLFW.GLFW_KEY_RIGHT -> {
                    model.increase()
                    return true
                }
            }
        }
        return false
    }

    override fun draw(x: Int, y: Int, width: Int, ctx: DrawingContext): Int {
        val sb: StringBuilder = ctx.stringBuilder
        sb.setLength(0)
        sb.append("[")
        val value = model.value()
        val def = model.definition()
        var k = def.minValue()
        while (k <= def.maxValue()) {
            if (k < value || k > value) {
                sb.append('-')
            } else {
                sb.append('|')
            }
            k += def.step()
        }
        sb.append("]")
        val start = sb.length
        sb.append(def.maxValue())
        val target = sb.length
        sb.setLength(start)
        sb.append(value)
        while (sb.length < target) {
            sb.insert(start, ' ')
        }
        return ctx.batch.draw(x, y, width, sb, ctx.textAttributes)
    }
}
