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

import com.github.ykiselev.common.IntDimensions;
import com.github.ykiselev.common.math.PowerOfTwo;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphBitmapBox;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.04.2019
 */
public final class TrueTypeFontInfo implements AutoCloseable {

    /**
     * This buffer is referenced as native address from {@link STBTTFontinfo} structure so we need to keep it
     * from being collected by gc
     */
    private final Wrap<ByteBuffer> ttf;

    private final STBTTFontinfo info;

    private final FontMetrics metrics;

    public ByteBuffer fontData() {
        return ttf.value();
    }

    public FontMetrics metrics() {
        return metrics;
    }

    /**
     * @param ttf     the raw fond data
     * @param info    the font info structure
     * @param metrics the font metrics
     */
    public TrueTypeFontInfo(Wrap<ByteBuffer> ttf, STBTTFontinfo info, FontMetrics metrics) {
        this.ttf = requireNonNull(ttf);
        this.info = requireNonNull(info);
        this.metrics = requireNonNull(metrics);
    }

    @Override
    public void close() {
        ttf.close();
        info.close();
    }

    @Override
    public String toString() {
        return "TrueTypeFontInfo{" +
                "ttf=" + ttf +
                ", info=" + info +
                ", metrics=" + metrics +
                '}';
    }

    public float getKernAdvance(int codePoint1, int codePoint2) {
        return stbtt_GetCodepointKernAdvance(info, codePoint1, codePoint2) * metrics.scale();
    }

    @Deprecated
    public IntDimensions calculateBitmapDimensions(int[] codePoints) {
        final int[] widths = new int[codePoints.length];
        final int[] heights = new int[codePoints.length];
        // Collect width and height of each glyph
        try (MemoryStack stack = stackPush()) {
            final IntBuffer advance = stack.mallocInt(1);
            final IntBuffer lsb = stack.mallocInt(1);
            final IntBuffer x0 = stack.mallocInt(1);
            final IntBuffer y0 = stack.mallocInt(1);
            final IntBuffer x1 = stack.mallocInt(1);
            final IntBuffer y1 = stack.mallocInt(1);

            for (int i = 0; i < codePoints.length; ++i) {
                final int codePoint = codePoints[i];
                final int g = stbtt_FindGlyphIndex(info, codePoint);
                stbtt_GetGlyphHMetrics(info, g, advance, lsb);
                stbtt_GetGlyphBitmapBox(info, g, metrics.scale(), metrics.scale(), x0, y0, x1, y1);
                widths[i] = x1.get(0) - x0.get(0);
                heights[i] = y1.get(0) - y0.get(0);
            }
        }
        final int sumWidth = Arrays.stream(widths).sum();
        int width = PowerOfTwo.next(sumWidth);
        int height = PowerOfTwo.next(Arrays.stream(heights).max().orElseThrow());
        for (; ; ) {
            final int w = width >> 1;
            final int h = height << 1;
            if (w <= h) {
                break;
            }
            width = w;
            height = h;
        }
        for (; ; ) {
            final int bottom = tryFit(widths, heights, width);
            if (bottom <= height) {
                break;
            }
            width <<= 1;
        }
        return new IntDimensions(width, height);
    }

    private int tryFit(int[] widths, int[] heights, int bitmapWidth) {
        int x = 1, y = 1, bottom = y;
        for (int i = 0; i < widths.length; i++) {
            final int w = widths[i] + 1;
            if (x + w >= bitmapWidth) {
                x = 1;
                y = bottom;
            }
            x += w;
            final int h = heights[i] + 1;
            if (y + h >= bottom) {
                bottom = y + h;
            }
        }
        return bottom;
    }

    public static TrueTypeFontInfo load(Wrap<ByteBuffer> fontData, float fontHeight) {
        final STBTTFontinfo info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, fontData.value())) {
            throw new IllegalStateException("Failed to initialize font information!");
        }

        try (MemoryStack stack = stackPush()) {
            final IntBuffer ascent = stack.mallocInt(1);
            final IntBuffer descent = stack.mallocInt(1);
            final IntBuffer lineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, ascent, descent, lineGap);

            return new TrueTypeFontInfo(
                    fontData,
                    info,
                    new FontMetrics(
                            stbtt_ScaleForPixelHeight(info, fontHeight),
                            ascent.get(0),
                            descent.get(0),
                            lineGap.get(0),
                            fontHeight
                    )
            );
        }
    }

}
