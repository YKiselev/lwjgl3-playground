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
import com.github.ykiselev.playground.ui.models.checkbox.CheckBoxModel
import com.github.ykiselev.spi.services.layers.DrawingContext
import org.lwjgl.glfw.GLFW

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CheckBox(val model: CheckBoxModel) : AbstractUiElement() {

    override fun onKey(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
        if (action == GLFW.GLFW_PRESS) {
            when (key) {
                GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_SPACE -> {
                    model.flip()
                    return true
                }
            }
        }
        return false
    }

    override fun draw(x: Int, y: Int, width: Int, ctx: DrawingContext): Int {
        return ctx.batch.draw(x, y, width, "[" + (if (model.checked()) "X" else " ") + "]", ctx.textAttributes)
    }
}
