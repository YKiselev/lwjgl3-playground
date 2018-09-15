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