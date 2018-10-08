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
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.wrap.Wrap;

import static java.util.Objects.requireNonNull;

/**
 * Created by Uze on 17.01.2015.
 */
public final class DefaultSpriteBatch implements SpriteBatch {

    private final TexturedQuads quads;

    private final Wrap<? extends Texture2d> white;

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
     * @param quads the textured quads to use
     * @param white the white texture (usually 1x1) for drawing solid color rectangles.
     */
    public DefaultSpriteBatch(TexturedQuads quads, Wrap<? extends Texture2d> white) {
        this.quads = requireNonNull(quads);
        this.white = requireNonNull(white);
    }

    /**
     * @param program the program to use
     * @param white   the white texture (usually 1x1) for drawing solid color rectangles.
     */
    public DefaultSpriteBatch(Wrap<ProgramObject> program, Wrap<? extends Texture2d> white) {
        this(new TexturedQuads(program), white);
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

    @Override
    public int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, TextAlignment alignment, int color) {
        quads.use(font.texture());

        final LineStart lineStart = LineStart.from(alignment);
        final int dy = font.height() + font.glyphYBorder();
        final int maxX = x + maxWidth;
        int fx = lineStart.calculate(x, font, text, 0, maxWidth);
        int fy = y - dy, qy = fy;
        for (int i = 0; i < text.length(); i++) {
            final char value = text.charAt(i);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                fx = lineStart.calculate(x, font, text, i + 1, maxWidth);
                fy -= dy;
                continue;
            }

            final Glyph glyph = font.glyphOrDefault(value);
            if (fx + glyph.width() > maxX) {
                fx = lineStart.calculate(x, font, text, i, maxWidth);
                fy -= dy;
            }

            final int x1 = fx + glyph.width();
            final int y1 = fy + font.height();
            if (value != ' ') {
                quads.addQuad(fx, fy, glyph.s0(), glyph.t1(), x1, y1, glyph.s1(), glyph.t0(), color);
                qy = fy;
            }

            fx = x1;
        }
        return y - qy;
    }

    @Override
    public void draw(TextBuilder builder, int x, int y) {
        builder.draw(quads, x, y);
    }

    @Override
    public void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color) {
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
