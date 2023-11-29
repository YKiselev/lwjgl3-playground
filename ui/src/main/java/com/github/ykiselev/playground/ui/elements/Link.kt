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
import com.github.ykiselev.spi.services.layers.DrawingContext
import org.lwjgl.glfw.GLFW
import java.util.*

/**
 * Menu item which is represented on UI as text. When "activated" executes provided action.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Link(val name: String, private val action: Runnable) : AbstractUiElement() {

    private var cx = 0.0
    private var cy = 0.0

    override fun onCursor(x: Double, y: Double) {
        cx = x
        cy = y
    }

    override fun onKey(key: Int, scanCode: Int, action: Int, mods: Int): Boolean {
        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_ENTER) {
                this.action.run()
                return true
            }
        }
        return false
    }

    override fun draw(x: Int, y: Int, width: Int, ctx: DrawingContext): Int {
        // todo - make use of hovers?
//        int color = 0xffffffff;
//        if (cx > x && cx < x + width && cy < y && cy > y - ctx.font().height()) {
//            color = 0xffff00ff;
//        }
        return ctx.batch.draw(x, y, width, name, ctx.textAttributes)
    }
}
