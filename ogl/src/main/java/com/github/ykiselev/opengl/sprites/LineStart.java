package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;

/**
 * Calculates start (left) x coordinate of line of text for different horizontal alignment modes.
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
            final Glyph glyph = font.glyphForCharacter(value);
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
