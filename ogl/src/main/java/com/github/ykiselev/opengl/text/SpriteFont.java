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

    Glyph glyphForCharacter(char ch);

    Glyph findGlyph(char ch);

    int width(CharSequence text);

    int height(CharSequence text, int width);

    @Override
    void close();
}
