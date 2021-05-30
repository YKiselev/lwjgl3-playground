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

package com.github.ykiselev.opengl.text;

import com.github.ykiselev.opengl.sprites.TexturedQuads;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
public interface Font extends AutoCloseable {

    int texture();

    int height();

    int lineSpace();

    int width(CharSequence text);

    int width(int codePoint);

    /**
     * Calculates actual height of {@code text}.
     *
     * @param text  the text to calculate height for.
     * @param width the maximum width to use.
     * @return the actual text height.
     */
    int height(CharSequence text, int width);

    /**
     * Adds textured quad for specified code point to {@code quads}.
     *
     * @param quads     the target textured quads
     * @param codePoint the code point to add quad for
     * @param x         the base x-coordinate
     * @param y         the base y-coordinate
     * @param color     the color to use
     */
    void addQuad(TexturedQuads quads, int codePoint, float x, float y, int color);

    default float getKernAdvance(int codePoint1, int codePoint2) {
        return 0f;
    }
}
