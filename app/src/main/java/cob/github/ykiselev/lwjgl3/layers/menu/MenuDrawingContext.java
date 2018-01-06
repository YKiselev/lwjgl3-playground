package cob.github.ykiselev.lwjgl3.layers.menu;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface MenuDrawingContext {

    SpriteFont font();

    SpriteBatch spriteBatch();

    int draw(int x, int y, int width, CharSequence text, int color);
}
