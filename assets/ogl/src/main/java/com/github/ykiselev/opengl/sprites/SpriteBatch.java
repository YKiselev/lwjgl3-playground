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

    /**
     * @param x                   the left viewport coordinate
     * @param y                   the bottom viewport coordinate
     * @param width               the width of viewport
     * @param height              the height of viewport
     * @param enableAlphaBlending set to {@code true} to use alpha-blending
     */
    void begin(int x, int y, int width, int height, boolean enableAlphaBlending);

    /**
     * Draws text at specified location with specified sprite font, maximum width and color.
     * </p>
     *
     * @param font     the sprite font to use
     * @param x        the left coordinate of the origin of the text bounding rectangle
     * @param y        the top coordinate of the origin of the text bounding rectangle
     * @param maxWidth the maximum width of bounding rectangle. When text width reaches this value next character is drawn as if there '\n' between next and previous characters.
     * @param text     the text to draw (possibly multi-line if there is '\n' characters in text or if maxWidth exceeded)
     * @param color    the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     * @return actual height of text
     */
    int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, int color);

    /**
     * Draws text at specified location with specified sprite font, maximum width and color.
     * </p>
     *
     * @param font      the sprite font to use
     * @param x         the left coordinate of the origin of the text bounding rectangle
     * @param y         the top coordinate of the origin of the text bounding rectangle
     * @param maxWidth  the maximum width of bounding rectangle. When text width reaches this value next character is drawn as if there '\n' between next and previous characters.
     * @param text      the text to draw (possibly multi-line if there is '\n' characters in text or if maxWidth exceeded)
     * @param alignment the text alignment
     * @param color     the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     * @return actual height of text
     */
    int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, TextAlignment alignment, int color);

    /**
     * Draws sprite represented as texture at specified location with specified width, height and color.
     * </p>
     *
     * @param texture the sprite to use
     * @param x       the left coordinate of the origin of the sprite
     * @param y       the bottom coordinate of the origin of the sprite
     * @param width   the width of sprite
     * @param height  the height of sprite
     * @param color   the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     */
    void draw(Texture2d texture, int x, int y, int width, int height, int color);

    void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color);

    void end();
}
