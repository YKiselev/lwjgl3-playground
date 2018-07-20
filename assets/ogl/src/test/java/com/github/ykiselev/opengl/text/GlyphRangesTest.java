package com.github.ykiselev.opengl.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class GlyphRangesTest {

    @Test
    void shouldFindGlyph() {
        final Glyph g1 = new Glyph(0, 0, 0, 0, 8);
        final Glyph g2 = new Glyph(0, 0, 0, 0, 8);
        final Glyph g3 = new Glyph(0, 0, 0, 0, 8);
        final Glyph g4 = new Glyph(0, 0, 0, 0, 8);
        final GlyphRange r1 = new GlyphRange('a', new Glyph[]{g1});
        final GlyphRange r2 = new GlyphRange('c', new Glyph[]{g2, g3});
        final GlyphRanges ranges = new GlyphRanges(new GlyphRange[]{r1, r2}, g4);
        assertEquals(g1, ranges.glyph('a'));
        assertEquals(g2, ranges.glyph('c'));
        assertEquals(g3, ranges.glyph('d'));
        assertNull(ranges.glyph('b'));
        assertNull(ranges.glyph('e'));
        assertEquals(g4, ranges.glyphOrDefault('b'));
        assertEquals(g4, ranges.glyphOrDefault('e'));
    }
}