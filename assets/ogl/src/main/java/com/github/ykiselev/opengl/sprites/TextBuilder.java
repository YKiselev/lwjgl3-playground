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

import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;

import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class TextBuilder {

    /**
     * Each quad has two 2d vertices with texture coordinates plus r,g,b,a values of color.
     */
    private static final int QUAD_FLOATS = 12;

    private int color = 0xffffffff;

    private TextAlignment alignment;

    private SpriteFont font;

    private int maxWidth;

    /**
     * Array of quads. Each quad occupies {@link TextBuilder#QUAD_FLOATS} elements of this array.
     */
    private float[] quads;

    /**
     * Number of quads added.
     */
    private int counter;

    public int color() {
        return color;
    }

    public void color(int color) {
        this.color = color;
    }

    public TextAlignment alignment() {
        return alignment;
    }

    public void alignment(TextAlignment alignment) {
        this.alignment = alignment;
    }

    public SpriteFont font() {
        return font;
    }

    public void font(SpriteFont font) {
        this.font = font;
    }

    public int maxWidth() {
        return maxWidth;
    }

    public void maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public TextBuilder(int capacity) {
        this.quads = new float[capacity * QUAD_FLOATS];
    }

    private void ensureQuadFits() {
        if ((counter + 1) * QUAD_FLOATS >= quads.length) {
            quads = Arrays.copyOf(quads, quads.length * 2);
        }
    }

    private void addQuad(float x0, float y0, float s0, float t0, float x1, float y1, float s1, float t1, int color) {
        ensureQuadFits();
        int i = counter * QUAD_FLOATS;
        quads[i++] = x0;
        quads[i++] = y0;
        quads[i++] = s0;
        quads[i++] = t0;
        quads[i++] = x1;
        quads[i++] = y1;
        quads[i++] = s1;
        quads[i++] = t1;
        Colors.putAsVector4(quads, i, color);
        counter++;
    }

    public int draw(CharSequence text) {
        if (alignment == null) {
            throw new NullPointerException("alignment");
        }
        if (font == null) {
            throw new NullPointerException("font");
        }
        if (maxWidth <= 0) {
            throw new IllegalArgumentException("Invalid maxWidth!");
        }
        final LineStart lineStart = LineStart.from(alignment);
        final int dy = font.height() + font.glyphYBorder();
        final int maxX = maxWidth;
        int fx = lineStart.calculate(0, font, text, 0, maxWidth);
        int fy = -dy, qy = fy;
        for (int i = 0; i < text.length(); i++) {
            final char value = text.charAt(i);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                fx = lineStart.calculate(0, font, text, i + 1, maxWidth);
                fy -= dy;
                continue;
            }

            final Glyph glyph = font.glyphOrDefault(value);
            if (fx + glyph.width() > maxX) {
                fx = lineStart.calculate(0, font, text, i, maxWidth);
                fy -= dy;
            }

            final int x1 = fx + glyph.width();
            final int y1 = fy + font.height();
            if (value != ' ') {
                addQuad(fx, fy, glyph.s0(), glyph.t1(), x1, y1, glyph.s1(), glyph.t0(), color);
                qy = fy;
            }

            fx = x1;
        }
        return -qy;
    }

    public void clear() {
        counter = 0;
    }

    public void draw(TexturedQuads tq, int x, int y) {
        if (font == null) {
            throw new IllegalStateException("Font not set!");
        }
        tq.use(font.texture());

        for (int i = 0; i < counter * QUAD_FLOATS; ) {
            tq.addQuad(
                    x + quads[i++],
                    y + quads[i++],
                    quads[i++],
                    quads[i++],
                    x + quads[i++],
                    y + quads[i++],
                    quads[i++],
                    quads[i++],
                    quads[i++],
                    quads[i++],
                    quads[i++],
                    quads[i++]
            );
        }
    }
}
