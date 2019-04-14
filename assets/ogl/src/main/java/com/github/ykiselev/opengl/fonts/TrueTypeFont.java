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

package com.github.ykiselev.opengl.fonts;

import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;

import java.nio.FloatBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
public final class TrueTypeFont implements AutoCloseable {

    private final Wrap<Texture2d> texture;

    private final int bitmapWidth;

    private final int bitmapHeight;

    private final STBTTPackedchar.Buffer chardata;

    private final Wrap<TrueTypeFontInfo> info;

    private final CodePoints codePoints;

    public float fontSize() {
        return info.value().metrics().fontSize();
    }

    public int texture() {
        return texture.value().id();
    }

    public int bitmapWidth() {
        return bitmapWidth;
    }

    public int bitmapHeight() {
        return bitmapHeight;
    }

    public TrueTypeFontInfo info() {
        return info.value();
    }

    public TrueTypeFont(Wrap<TrueTypeFontInfo> info, STBTTPackedchar.Buffer chardata, CodePoints codePoints, Wrap<Texture2d> texture, int bitmapWidth, int bitmapHeight) {
        this.info = requireNonNull(info);
        this.chardata = requireNonNull(chardata);
        this.codePoints = requireNonNull(codePoints);
        this.texture = requireNonNull(texture);
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
    }

    @Override
    public void close() {
        chardata.close();
        info.close();
        texture.close();
        info.close();
    }

    @Deprecated
    public void getPackedQuad(int codePoint, FloatBuffer xb, FloatBuffer yb, STBTTAlignedQuad quad) {
        stbtt_GetPackedQuad(chardata, bitmapWidth, bitmapHeight, codePoints.indexOf(codePoint), xb, yb, quad, false);
    }

    public STBTTPackedchar.Buffer charData(int codePoint) {
        return chardata.position(codePoints.indexOf(codePoint));
    }

/* todo - move all drawing code to the sprite batch

    public void print(float x, float y, String text) {
        print(x, y, text, true);
    }

    public void print(float x, float y, String text, boolean kerning) {
        xb.put(0, x);
        yb.put(0, y);

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);

        try (MemoryStack stack = stackPush()) {
            glBegin(GL_QUADS);
            for (int i = 0; i < text.length(); ) {
                final int cp = text.codePointAt(i);
                i += Character.charCount(cp);

                stbtt_GetPackedQuad(chardata, bitmapWidth, bitmapHeight, codePoints.indexOf(cp), xb, yb, q, false);
                // apply some kerning
                if (kerning && i < text.length()) {
                    final int cp2 = text.codePointAt(i);
                    final float advance = info.getKernAdvance(cp, cp2);
                    xb.put(0, xb.get(0) + advance);
                }
                drawBoxTC(
                        q.x0(), q.y0(), q.x1(), q.y1(),
                        q.s0(), q.t0(), q.s1(), q.t1()
                );
            }
            glEnd();
        }
    }

    private static void drawBoxTC(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1) {
        glTexCoord2f(s0, t0);
        glVertex2f(x0, y0);
        glTexCoord2f(s1, t0);
        glVertex2f(x1, y0);
        glTexCoord2f(s1, t1);
        glVertex2f(x1, y1);
        glTexCoord2f(s0, t1);
        glVertex2f(x0, y1);
    }
*/
}
