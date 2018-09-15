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

package com.github.ykiselev.opengl.text;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GlyphRanges {

    private final GlyphRange[] ranges;

    private final Glyph defaultGlyph;

    public GlyphRanges(GlyphRange[] ranges, Glyph defaultGlyph) {
        this.ranges = ranges.clone();
        this.defaultGlyph = requireNonNull(defaultGlyph);
    }

    public Glyph glyphOrDefault(char ch) {
        final Glyph g = glyph(ch);
        return g != null ? g : defaultGlyph;
    }

    public Glyph glyph(char ch) {
        return find(ch, 0, ranges.length);
    }

    /**
     * @param ch    the char o find glyph for
     * @param start the start index (inclusive)
     * @param end   end index (exclusive)
     * @return the character's glyph or {@code null}
     */
    private Glyph find(char ch, int start, int end) {
        if (start >= end) {
            return null;
        }
        final int center = (start + end) / 2;
        final GlyphRange range = ranges[center];
        final int sign = range.classify(ch);
        if (sign > 0) {
            return find(ch, center + 1, end);
        } else if (sign < 0) {
            return find(ch, start, center);
        }
        return range.glyph(ch);
    }
}
