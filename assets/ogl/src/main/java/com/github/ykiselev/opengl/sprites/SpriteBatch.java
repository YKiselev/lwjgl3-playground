package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SpriteBatch extends AutoCloseable {

    int width();

    int height();

    int drawCount();

    void begin(int x, int y, int width, int height, boolean enableAlphaBlending);

    int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, int color);

    int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, TextAlignment alignment, int color);

    void draw(Texture2d texture, int x, int y, int width, int height, int color);

    void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color);

    void end();
}
