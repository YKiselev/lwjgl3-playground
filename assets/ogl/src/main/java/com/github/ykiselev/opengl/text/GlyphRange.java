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

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GlyphRange {

    private final char start;

    private final Glyph[] glyphs;

    public GlyphRange(char start, Glyph[] glyphs) {
        this.start = start;
        this.glyphs = glyphs.clone();
    }

    public int classify(char ch) {
        if (ch < start) {
            return -1;
        } else if (ch >= start + glyphs.length) {
            return 1;
        }
        return 0;
    }

    public Glyph glyph(char ch) {
        return glyphs[ch - start];
    }

    @Override
    public String toString() {
        return "GlyphRange{" +
                "start=" + start +
                '}';
    }
}
