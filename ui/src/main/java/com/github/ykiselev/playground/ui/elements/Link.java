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

package com.github.ykiselev.playground.ui.elements;


import com.github.ykiselev.playground.ui.AbstractUiElement;
import com.github.ykiselev.spi.services.layers.DrawingContext;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * Menu item which is represented on UI as text. When "activated" executes provided action.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Link extends AbstractUiElement {

    private final String name;

    private final Runnable action;

    private double cx, cy;

    public Link(String name, Runnable action) {
        this.name = requireNonNull(name);
        this.action = requireNonNull(action);
    }

    @Override
    protected void onCursor(double x, double y) {
        cx = x;
        cy = y;
    }

    @Override
    protected boolean onKey(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_ENTER) {
                this.action.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, DrawingContext ctx) {
        // todo - make use of hovers?
//        int color = 0xffffffff;
//        if (cx > x && cx < x + width && cy < y && cy > y - ctx.font().height()) {
//            color = 0xffff00ff;
//        }
        return ctx.batch().draw(x, y, width, name, ctx.textAttributes());
    }
}
