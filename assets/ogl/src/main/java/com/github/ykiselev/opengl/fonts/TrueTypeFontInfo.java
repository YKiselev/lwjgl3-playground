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
import com.github.ykiselev.math.PowerOfTwo;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
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
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final ByteBuffer ttf;

    private final STBTTFontinfo info;

    private final float scale;

    private final int ascent;

    private final int descent;

    private final int lineGap;

    private final float lineHeight;

    public float scale() {
        return scale;
    }

    public int ascent() {
        return ascent;
    }

    public int descent() {
        return descent;
    }

    public int lineGap() {
        return lineGap;
    }

    public float lineHeight() {
        return lineHeight;
    }

    /**
     * @param ttf     the raw fond data
     * @param info    the font info structure
     * @param scale   see {@link STBTruetype#stbtt_ScaleForPixelHeight(org.lwjgl.stb.STBTTFontinfo, float)} for explanation
     * @param ascent  the font's ascent value
     * @param descent the font's descent value
     * @param lineGap the font's line gap
     */
    public TrueTypeFontInfo(ByteBuffer ttf, STBTTFontinfo info, float scale, int ascent, int descent, int lineGap) {
        this.ttf = requireNonNull(ttf);
        this.info = requireNonNull(info);
        this.scale = scale;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.lineHeight = scale * (ascent - descent + lineGap);
    }

    @Override
    public void close() {
        info.close();
    }

    public float getKernAdvance(int codePoint1, int codePoint2) {
        return stbtt_GetCodepointKernAdvance(info, codePoint1, codePoint2) * scale;
    }

    public void getCodePointHMetrics(int codePoint, @Nullable IntBuffer advanceWidth, @Nullable IntBuffer leftSideBearing) {
        stbtt_GetCodepointHMetrics(info, codePoint, advanceWidth, leftSideBearing);
    }

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
                stbtt_GetGlyphBitmapBox(info, g, scale, scale, x0, y0, x1, y1);
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

    public float measureWidth(String text, int from, int to, boolean useKerning) {
        int width = 0, maxWidth = 0;

        try (MemoryStack stack = stackPush()) {
            final IntBuffer advanceWidth = stack.mallocInt(1);

            int i = from;
            while (i < to) {
                final int cp = text.codePointAt(i);
                i += Character.charCount(cp);
                if (cp == '\n') {
                    if (width > maxWidth) {
                        maxWidth = width;
                    }
                    width = 0;
                    continue;
                }

                getCodePointHMetrics(cp, advanceWidth, null);
                width += advanceWidth.get(0);

                if (useKerning && i < to) {
                    final int cp2 = text.codePointAt(i);
                    width += getKernAdvance(cp, cp2);
                }
            }
        }
        if (width > maxWidth) {
            maxWidth = width;
        }
        return maxWidth * scale;
    }

    public static TrueTypeFontInfo load(ByteBuffer fontData, float fontHeight) {
        final STBTTFontinfo info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, fontData)) {
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
                    stbtt_ScaleForPixelHeight(info, fontHeight),
                    ascent.get(0),
                    descent.get(0),
                    lineGap.get(0)
            );
        }
    }

}
