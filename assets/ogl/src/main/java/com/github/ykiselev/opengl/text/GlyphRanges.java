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
