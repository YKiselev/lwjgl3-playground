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
    }

    @Deprecated
    public void getPackedQuad(int codePoint, FloatBuffer xb, FloatBuffer yb, STBTTAlignedQuad quad) {
        stbtt_GetPackedQuad(chardata, bitmapWidth, bitmapHeight, codePoints.indexOf(codePoint), xb, yb, quad, false);
    }

    public STBTTPackedchar.Buffer charData(int codePoint) {
        return chardata.position(codePoints.indexOf(codePoint));
    }
}
