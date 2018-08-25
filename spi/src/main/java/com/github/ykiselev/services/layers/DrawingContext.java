package com.github.ykiselev.services.layers;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface DrawingContext {

    SpriteFont font();

    SpriteBatch batch();

    StringBuilder stringBuilder();

    int draw(int x, int y, int width, CharSequence text, int color);
}
