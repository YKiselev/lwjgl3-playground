package com.github.ykiselev.services.layers;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.text.SpriteFont;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface DrawingContext {

    SpriteFont font();

    SpriteBatch batch();

    int draw(int x, int y, int width, CharSequence text, TextAlignment alignment, int color);

    StringBuilder stringBuilder();

    int draw(int x, int y, int width, CharSequence text, int color);

    void fill(int x, int y, int width, int height, int color);
}