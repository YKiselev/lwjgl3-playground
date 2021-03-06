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

import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.text.Font;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.wrap.Wrap;

import static java.util.Objects.requireNonNull;

/**
 * Created by Uze on 17.01.2015.
 */
public final class DefaultSpriteBatch implements SpriteBatch {

    private final TexturedQuads quads;

    private final Wrap<? extends Texture2d> white;

    private final ColorTable colorTable;

    @Override
    public int width() {
        return quads.width();
    }

    @Override
    public int height() {
        return quads.height();
    }

    @Override
    public int drawCount() {
        return quads.drawCount();
    }

    /**
     * @param quads      the textured quads to use
     * @param white      the white texture (usually 1x1) for drawing solid color rectangles.
     * @param colorTable the color table to use with color control sequences
     */
    public DefaultSpriteBatch(TexturedQuads quads, Wrap<? extends Texture2d> white, ColorTable colorTable) {
        this.quads = requireNonNull(quads);
        this.white = requireNonNull(white);
        this.colorTable = requireNonNull(colorTable);
    }

    /**
     * @param program    the program to use
     * @param white      the white texture (usually 1x1) for drawing solid color rectangles.
     * @param colorTable the color table to use with color control sequences
     */
    public DefaultSpriteBatch(Wrap<ProgramObject> program, Wrap<? extends Texture2d> white, ColorTable colorTable) {
        this(new TexturedQuads(program), white, colorTable);
    }

    @Override
    public void close() {
        quads.close();
        white.close();
    }

    @Override
    public void begin(int x, int y, int width, int height, boolean enableAlphaBlending) {
        quads.begin(x, y, width, height, enableAlphaBlending);
    }

    /**
     * Converts passed character value representing hexadecimal digit to it's integer value (i.e. 'A' -> 10).
     *
     * @param v the hexadecimal digit
     * @return integer value
     */
    private static int hexDigit(char v) {
        if (v >= '0' && v <= '9') {
            return v - '0';
        }
        if (v >= 'a' && v <= 'f') {
            return 10 + v - 'a';
        }
        if (v >= 'A' && v <= 'F') {
            return 10 + v - 'A';
        }
        return 0;
    }

    /**
     * Converts two-character hexadecimal sequence into index in color table and returns that color.
     *
     * @param c1 high digit
     * @param c2 low digit
     * @return the color from color table at index specified by index defined by passed characters.
     */
    private static int colorIndex(char c1, char c2) {
        return 16 * hexDigit(c1) + hexDigit(c2);
    }

    @Override
    public int draw(int x, int y, int maxWidth, CharSequence text, TextAttributes attributes) {
        final Font font = attributes.font();
        if (font == null) {
            throw new NullPointerException("Font is not set!");
        }
        quads.use(font.texture());

        final boolean useColorControlSequences = attributes.useColorControlSequences();
        final boolean useKerning = attributes.useKerning();
        final LineStart lineStart = LineStart.from(attributes.alignment());
        int color = attributes.color();
        final float dy = font.lineSpace();
        final int maxX = x + maxWidth;
        int pcp = 0;
        float fx = lineStart.calculate(x, font, text, 0, maxWidth);
        float fy = y - dy, qy = fy;
        for (int i = 0; i < text.length(); ) {
            final int previous = pcp;
            final int value = Character.codePointAt(text, i);
            pcp = value;
            i += Character.charCount(value);
            if (useColorControlSequences && value == '^' && (previous != '\\')) {
                if (i + 2 < text.length()) {
                    color = colorTable.color(
                            colorIndex(text.charAt(++i), text.charAt(++i))
                    );
                }
                continue;
            }
            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                fx = lineStart.calculate(x, font, text, i + 1, maxWidth);
                fy -= dy;
                continue;
            }

            final int xadvance = font.width(value);
            if (fx + xadvance > maxX) {
                fx = lineStart.calculate(x, font, text, i, maxWidth);
                fy -= dy;
            }
            qy = fy;

            if (value != ' ') {
                font.addQuad(quads, value, fx, fy, color);
            }

            fx += xadvance;

            // apply some kerning
            if (useKerning && i < text.length()) {
                fx += font.getKernAdvance(value, Character.codePointAt(text, i));
            }
        }

        return (int) (y - qy);
    }

    @Override
    public void draw(int texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color) {
        quads.use(texture);
        quads.addQuad(x, y, s0, t0, x + width, y + height, s1, t1, color);
    }

    @Override
    public void fill(int x, int y, int width, int height, int color) {
        draw(white.value(), x, y, width, height, color);
    }

    @Override
    public void end() {
        quads.end();
    }
}
