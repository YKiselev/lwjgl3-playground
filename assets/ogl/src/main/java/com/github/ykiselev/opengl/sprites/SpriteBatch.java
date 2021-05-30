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

package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.textures.Texture2d;

/**
 * Interface with convenient methods for 2d operations like drawing sprites, text, filling rects.
 * Note that coordinate system starts at lower left corner. Also note that text methods y-origin denotes top corner of
 * the bounding box while sprite drawing methods y-origin is bottom corner.
 *<pre>
 *     (0,0)----------
 *     |   T e x t   |
 *     ---------------
 *
 *     ---------------
 *     | S p r i t e |
 *     (0,0)----------
 *</pre>
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SpriteBatch extends AutoCloseable {

    int width();

    int height();

    int drawCount();

    /**
     * @param x                   the left viewport coordinate
     * @param y                   the bottom viewport coordinate
     * @param width               the width of viewport
     * @param height              the height of viewport
     * @param enableAlphaBlending set to {@code true} to use alpha-blending
     */
    void begin(int x, int y, int width, int height, boolean enableAlphaBlending);

    /**
     * Draws text at specified location with specified sprite font, maximum width and color.
     * </p>
     *
     * @param x          the left coordinate of the origin of the text bounding rectangle
     * @param y          the top coordinate of the origin of the text bounding rectangle
     * @param maxWidth   the maximum width of bounding rectangle. When text width reaches this value next character is drawn as if there '\n' between next and previous characters.
     * @param text       the text to draw (possibly multi-line if there is '\n' characters in text or if maxWidth exceeded)
     * @param attributes the attributes to use.
     * @return actual height of text
     */
    int draw(int x, int y, int maxWidth, CharSequence text, TextAttributes attributes);

    /**
     * Draws sprite represented as texture at specified location with specified width, height and color.
     * </p>
     *
     * @param texture the sprite to use
     * @param x       the left coordinate of the origin of the sprite
     * @param y       the bottom coordinate of the origin of the sprite
     * @param width   the width of sprite
     * @param height  the height of sprite
     * @param color   the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     */
    default void draw(Texture2d texture, int x, int y, int width, int height, int color) {
        draw(texture, x, y, width, height, 0f, 1f, 1f, 0f, color);
    }

    /**
     * Draws sprite represented as texture at specified location with specified width, height and color.
     * </p>
     *
     * @param texture texture to use
     * @param x       the left coordinate of the origin of the sprite
     * @param y       the bottom coordinate of the origin of the sprite
     * @param width   the width of sprite
     * @param height  the height of sprite
     * @param color   the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     */
    default void draw(int texture, int x, int y, int width, int height, int color) {
        draw(texture, x, y, width, height, 0f, 1f, 1f, 0f, color);
    }

    default void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color) {
        draw(texture.id(), x, y, width, height, s0, t0, s1, t1, color);
    }

    void draw(int texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color);

    void fill(int x, int y, int width, int height, int color);

    void end();
}