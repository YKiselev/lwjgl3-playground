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

package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;

/**
 * Calculates start (left) x coordinate of the line of text with respect to different horizontal alignment modes.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
enum LineStart {

    ALIGN_LEFT {
        @Override
        int calculate(int x0, SpriteFont font, CharSequence text, int fromIndex, int maxWidth) {
            return x0;
        }
    },
    ALIGN_MIDDLE {
        @Override
        int calculate(int x0, SpriteFont font, CharSequence text, int fromIndex, int maxWidth) {
            return x0 + (maxWidth - lineWidth(font, text, fromIndex, maxWidth)) / 2;
        }
    },
    ALIGN_RIGHT {
        @Override
        int calculate(int x0, SpriteFont font, CharSequence text, int fromIndex, int maxWidth) {
            return x0 + maxWidth - lineWidth(font, text, fromIndex, maxWidth);
        }
    };

    abstract int calculate(int x0, SpriteFont font, CharSequence text, int fromIndex, int maxWidth);

    /**
     * Calculates width of single line of supplied text starting from character at specified index.
     *
     * @param font      the sprite font
     * @param text      the text
     * @param fromIndex the first character to start from
     * @param maxWidth  the maximum line width
     * @return the width of single text line
     */
    private static int lineWidth(SpriteFont font, CharSequence text, int fromIndex, int maxWidth) {
        if (fromIndex >= text.length()) {
            return 0;
        }
        int result = 0;
        for (int i = fromIndex; i < text.length(); i++) {
            final char value = text.charAt(i);
            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                break;
            }
            final Glyph glyph = font.glyphOrDefault(value);
            if (result + glyph.width() > maxWidth) {
                break;
            }
            result += glyph.width();
        }
        return result;
    }

    public static LineStart from(TextAlignment alignment) {
        switch (alignment) {
            case LEFT:
                return LineStart.ALIGN_LEFT;

            case MIDDLE:
                return LineStart.ALIGN_MIDDLE;

            case RIGHT:
                return LineStart.ALIGN_RIGHT;

            default:
                throw new IllegalArgumentException("Unknown value: " + alignment);
        }
    }
}
