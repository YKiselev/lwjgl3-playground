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

package com.github.ykiselev.playground.ui;

import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.window.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiElement extends WindowEvents {

    /**
     * @param x     the x coordinate to start drawing at
     * @param y     the y coordinate to start drawing at
     * @param width the width of drawing area
     * @param ctx   the drawing context to use
     * @return the actual height of area occupied by ui element
     */
    int draw(int x, int y, int width, DrawingContext ctx);

    boolean isEnabled();

    void enable(boolean value);

    boolean isVisible();

    void visible(boolean value);
}
