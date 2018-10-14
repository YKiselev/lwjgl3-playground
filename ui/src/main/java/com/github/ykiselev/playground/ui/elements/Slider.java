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
import com.github.ykiselev.playground.ui.models.slider.SliderDefinition;
import com.github.ykiselev.playground.ui.models.slider.SliderModel;
import com.github.ykiselev.services.layers.DrawingContext;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Slider extends AbstractUiElement {

    private final SliderModel model;

    public Slider(SliderModel model) {
        this.model = requireNonNull(model);
    }

    @Override
    protected boolean onKey(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_LEFT:
                    model.decrease();
                    return true;

                case GLFW_KEY_RIGHT:
                    model.increase();
                    return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, DrawingContext ctx) {
        final StringBuilder sb = ctx.stringBuilder();
        sb.setLength(0);
        sb.append("[");
        final int value = model.value();
        final SliderDefinition def = model.definition();
        for (int k = def.minValue(); k <= def.maxValue(); k += def.step()) {
            if (k < value || k > value) {
                sb.append('-');
            } else {
                sb.append('|');
            }
        }
        sb.append("]");
        final int start = sb.length();
        sb.append(def.maxValue());
        final int target = sb.length();
        sb.setLength(start);
        sb.append(value);
        while (sb.length() < target) {
            sb.insert(start, ' ');
        }
        return ctx.batch().draw(x, y, width, sb, ctx.textAttributes());
    }
}
