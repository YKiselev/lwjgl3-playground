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

import com.github.ykiselev.lifetime.Manageable;
import com.github.ykiselev.opengl.textures.Texture2d;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Created by Uze on 14.01.2015.
 */
public final class SpriteFont implements AutoCloseable, Manageable<SpriteFont> {

    private final Description description;

    private final Consumer<SpriteFont> onClose;

    public Texture2d texture() {
        return description.texture;
    }

    public int fontHeight() {
        return description.fontHeight;
    }

    public int glyphXBorder() {
        return description.glyphXBorder;
    }

    public int glyphYBorder() {
        return description.glyphYBorder;
    }

    private SpriteFont(Description description, Consumer<SpriteFont> onClose) {
        this.description = requireNonNull(description);
        this.onClose = requireNonNull(onClose);
    }

    public SpriteFont(Texture2d texture, int fontHeight, int glyphXBorder, int glyphYBorder, GlyphRange[] ranges, Glyph defaultGlyph) {
        this(
                new Description(texture, fontHeight, glyphXBorder, glyphYBorder, ranges, defaultGlyph),
                c -> c.description.delete()
        );
    }

    @Override
    public SpriteFont manage(Consumer<SpriteFont> onClose) {
        return new SpriteFont(description, onClose);
    }

    @Override
    public void close() {
        onClose.accept(this);
    }

    public Glyph glyphForCharacter(char ch) {
        final Glyph g = findGlyph(ch);
        return g != null ? g : description.defaultGlyph;
    }

    public Glyph findGlyph(char ch) {
        for (GlyphRange range : description.ranges) {
            final Glyph glyph = range.glyphForCharacter(ch);
            if (glyph != null) {
                return glyph;
            }
        }
        return null;
    }

    /**
     * Calculates width of text. If text is multi-line then width of longest line is returned.
     *
     * @param text the text to measure width for
     * @return the width of text
     */
    public int width(CharSequence text) {
        int w = 0, maxWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            final char value = text.charAt(i);
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
            w += glyphForCharacter(value).width();
        }
        if (w > maxWidth) {
            maxWidth = w;
        }
        return maxWidth;
    }

    /**
     * Calculates height of the text with respect to possible new lines.
     *
     * @param text  the text to measure height for
     * @param width the maximum width of text. Any line exceeding this width will be wrapped just like when new line character is encountered in text.
     * @return the height of text
     */
    public int height(CharSequence text, int width) {
        final int dy = description.fontHeight + description.glyphYBorder;
        int lines = 0;
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            final char value = text.charAt(i);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                lines++;
                w = 0;
                continue;
            }

            final Glyph glyph = glyphForCharacter(value);

            if (w + glyph.width() > width) {
                lines++;
                w = glyph.width();
            }

            w += glyph.width();
        }

        if (w > 0) {
            lines++;
        }

        return lines * dy;
    }

    private static final class Description {

        private final Texture2d texture;

        private final int fontHeight;

        private final int glyphXBorder;

        private final int glyphYBorder;

        private final GlyphRange[] ranges;

        private final Glyph defaultGlyph;

        Description(Texture2d texture, int fontHeight, int glyphXBorder, int glyphYBorder, GlyphRange[] ranges, Glyph defaultGlyph) {
            this.texture = texture;
            this.fontHeight = fontHeight;
            this.glyphXBorder = glyphXBorder;
            this.glyphYBorder = glyphYBorder;
            this.ranges = ranges;
            this.defaultGlyph = defaultGlyph;
        }

        void delete() {
            texture.close();
        }

    }
}
