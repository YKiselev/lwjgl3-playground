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

import com.github.ykiselev.opengl.sprites.TexturedQuads;
import com.github.ykiselev.opengl.text.Font;
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
public final class TrueTypeFont implements Font, AutoCloseable {

    private final Wrap<Texture2d> texture;

    private final int bitmapWidth;

    private final int bitmapHeight;

    private final float ibw, ibh;

    private final STBTTPackedchar.Buffer charData;

    private final Wrap<TrueTypeFontInfo> info;

    private final CodePoints codePoints;

    private final int defaultIndex;

    @Override
    public int height() {
        return (int) info.value().metrics().fontSize();
    }

    @Override
    public int lineSpace() {
        return (int) info.value().metrics().lineHeight();
    }

    @Override
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

    public TrueTypeFont(Wrap<TrueTypeFontInfo> info, STBTTPackedchar.Buffer charData, CodePoints codePoints, Wrap<Texture2d> texture, int bitmapWidth, int bitmapHeight) {
        this.info = requireNonNull(info);
        this.charData = requireNonNull(charData);
        this.codePoints = requireNonNull(codePoints);
        this.texture = requireNonNull(texture);
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.ibw = 1f / bitmapWidth;
        this.ibh = 1f / bitmapHeight;
        this.defaultIndex = codePoints.indexOf(' ');
    }

    @Override
    public void close() {
        charData.close();
        info.close();
        texture.close();
    }

    @Deprecated
    public void getPackedQuad(int codePoint, FloatBuffer xb, FloatBuffer yb, STBTTAlignedQuad quad) {
        stbtt_GetPackedQuad(charData, bitmapWidth, bitmapHeight, codePoints.indexOf(codePoint), xb, yb, quad, false);
    }

    public STBTTPackedchar.Buffer charData(int codePoint) {
        return charData.position(codePoints.indexOf(codePoint, defaultIndex));
    }

    @Override
    public int width(CharSequence text) {
        int w = 0, maxWidth = 0;
        for (int i = 0; i < text.length(); ) {
            final int value = Character.codePointAt(text, i);
            i += Character.charCount(value);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                if (w > maxWidth) {
                    maxWidth = w;
                }
                w = 0;
                continue;
            }
            final STBTTPackedchar.Buffer charData = charData(value);
            final int xadvance = (int) charData.xadvance();

            w += xadvance;
        }
        if (w > maxWidth) {
            maxWidth = w;
        }
        return maxWidth;
    }

    @Override
    public int width(int codePoint) {
        return (int) charData(codePoint).xadvance();
    }

    @Override
    public int height(CharSequence text, int width) {
        int lines = 0;
        int w = 0;
        for (int i = 0; i < text.length(); ) {
            final int value = Character.codePointAt(text, i);
            i += Character.charCount(value);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                lines++;
                w = 0;
                continue;
            }

            final STBTTPackedchar.Buffer charData = charData(value);
            final int xadvance = (int) charData.xadvance();

            if (w + xadvance > width) {
                lines++;
                w = xadvance;
            }

            w += xadvance;
        }

        if (w > 0) {
            lines++;
        }

        return (int) (lines * info.value().metrics().lineHeight());
    }

    @Override
    public void addQuad(TexturedQuads quads, int codePoint, float x, float y, int color) {
        final STBTTPackedchar.Buffer charData = charData(codePoint);

        final float qx0 = x + charData.xoff();
        final float qy0 = y - charData.yoff();
        final float qx1 = x + charData.xoff2();
        final float qy1 = y - charData.yoff2();

        final float s0 = charData.x0() * ibw;
        final float t0 = charData.y0() * ibh;
        final float s1 = charData.x1() * ibw;
        final float t1 = charData.y1() * ibh;

        quads.addQuad(qx0, qy1, s0, t1, qx1, qy0, s1, t0, color);
    }

    @Override
    public float getKernAdvance(int codePoint1, int codePoint2) {
        return info().getKernAdvance(codePoint1, codePoint2);
    }
}
