package com.github.ykiselev.opengl.text;

import com.github.ykiselev.opengl.textures.Texture2d;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SpriteFont extends AutoCloseable {

    Texture2d texture();

    int fontHeight();

    int glyphXBorder();

    int glyphYBorder();

    /**
     * Returns character's glyph or default character glyph
     *
     * @param ch character to search glyph for
     * @return character glyph if found, default character glyph otherwise
     */
    Glyph glyphOrDefault(char ch);

    /**
     * Searches for character glyph.
     *
     * @param ch character to search glyph for
     * @return character glyph if found {@code null} otherwise
     */
    Glyph glyph(char ch);

    int width(CharSequence text);

    int height(CharSequence text, int width);

    @Override
    void close();
}
