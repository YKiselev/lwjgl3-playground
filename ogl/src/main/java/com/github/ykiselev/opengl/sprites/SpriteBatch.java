package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;

import static java.util.Objects.requireNonNull;

/**
 * Created by Uze on 17.01.2015.
 */
public final class SpriteBatch implements AutoCloseable {

    private final TexturedQuads quads;

    public int width() {
        return quads.width();
    }

    public int height() {
        return quads.height();
    }

    public int drawCount() {
        return quads.drawCount();
    }

    /**
     * @param quads the textured quads to use
     */
    public SpriteBatch(TexturedQuads quads) {
        this.quads = requireNonNull(quads);
    }

    /**
     * Note that this class doesn't take ownership over supplied program and hence won't call {@link ProgramObject#close()} method on close!
     *
     * @param program the program to use
     */
    public SpriteBatch(ProgramObject program) {
        this(new TexturedQuads(program));
    }

    @Override
    public void close() {
        quads.close();
    }

    /**
     * @param x                   the left viewport coordinate
     * @param y                   the bottom viewport coordinate
     * @param width               the width of viewport
     * @param height              the height of viewport
     * @param enableAlphaBlending set to {@code true} to use alpha-blending
     */
    public void begin(int x, int y, int width, int height, boolean enableAlphaBlending) {
        quads.begin(x, y, width, height, enableAlphaBlending);
    }

    /**
     * Draws text at specified location with specified sprite font, maximum width and color.
     * </p>
     *
     * @param font     the sprite font to use
     * @param x        the left coordinate of the origin of the text bounding rectangle
     * @param y        the bottom coordinate of the origin of the text bounding rectangle
     * @param maxWidth the maximum width of bounding rectangle. When text width reaches this value next character is drawn as if there '\n' between next and previous characters.
     * @param text     the text to draw (possibly multi-line if there is '\n' characters in text or if maxWidth exceeded)
     * @param color    the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     * @return actual height of text
     */
    public int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, int color) {
        return draw(font, x, y, maxWidth, text, TextAlignment.LEFT, color);
    }

    /**
     * Draws text at specified location with specified sprite font, maximum width and color.
     * </p>
     *
     * @param font      the sprite font to use
     * @param x         the left coordinate of the origin of the text bounding rectangle
     * @param y         the bottom coordinate of the origin of the text bounding rectangle
     * @param maxWidth  the maximum width of bounding rectangle. When text width reaches this value next character is drawn as if there '\n' between next and previous characters.
     * @param text      the text to draw (possibly multi-line if there is '\n' characters in text or if maxWidth exceeded)
     * @param alignment the text alignment
     * @param color     the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     * @return actual height of text
     */
    public int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, TextAlignment alignment, int color) {
        quads.use(font.texture());

        final LineStart lineStart = LineStart.from(alignment);
        final int dy = font.fontHeight() + font.glyphYBorder();
        final int maxX = x + maxWidth;
        int fx = lineStart.calculate(x, font, text, 0, maxWidth);
        int fy = y;
        for (int i = 0; i < text.length(); i++) {
            final char value = text.charAt(i);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                fx = lineStart.calculate(x, font, text, i + 1, maxWidth);
                fy -= dy;
                continue;
            }

            final Glyph glyph = font.glyphForCharacter(value);
            if (fx + glyph.width() > maxX) {
                fx = lineStart.calculate(x, font, text, i, maxWidth);
                fy -= dy;
            }

            final int x1 = fx + glyph.width();
            final int y1 = fy + font.fontHeight();
            if (value != ' ') {
                quads.addQuad(fx, fy, glyph.s0(), glyph.t0(), x1, y1, glyph.s1(), glyph.t1(), color);
            }

            fx = x1;
        }
        return y - fy + dy;
    }

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
    public void draw(Texture2d texture, int x, int y, int width, int height, int color) {
        quads.use(texture);
        quads.addQuad(x, y, 0f, 0f, x + width, y + height, 1f, 1f, color);
    }

    public void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color) {
        quads.use(texture);
        quads.addQuad(x, y, s0, t0, x + width, y + height, s1, t1, color);
    }

    public void end() {
        quads.end();
    }
}
