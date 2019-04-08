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

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 08.04.2019
 */
public final class FontMetrics {

    private final float scale;

    private final int ascent;

    private final int descent;

    private final int lineGap;

    private final float lineHeight;

    private final float fontSize;

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

    public float fontSize() {
        return fontSize;
    }

    /**
     * @param scale    see {@link STBTruetype#stbtt_ScaleForPixelHeight(STBTTFontinfo, float)} for explanation
     * @param ascent   the font's ascent value
     * @param descent  the font's descent value
     * @param lineGap  the font's line gap
     * @param fontSize the scaled font size in pixels
     */
    public FontMetrics(float scale, int ascent, int descent, int lineGap, float fontSize) {
        this.scale = scale;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.lineHeight = scale * (ascent - descent + lineGap);
        this.fontSize = fontSize;
    }

    @Override
    public String toString() {
        return "FontMetrics{" +
                "scale=" + scale +
                ", ascent=" + ascent +
                ", descent=" + descent +
                ", lineGap=" + lineGap +
                ", lineHeight=" + lineHeight +
                ", fontSize=" + fontSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontMetrics that = (FontMetrics) o;
        return Float.compare(that.scale, scale) == 0 &&
                ascent == that.ascent &&
                descent == that.descent &&
                lineGap == that.lineGap &&
                Float.compare(that.lineHeight, lineHeight) == 0 &&
                Float.compare(that.fontSize, fontSize) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scale, ascent, descent, lineGap, lineHeight, fontSize);
    }
}
