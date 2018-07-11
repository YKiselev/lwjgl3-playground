package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.wrap.Wrap;

import static java.util.Objects.requireNonNull;

/**
 * Created by Uze on 17.01.2015.
 */
public final class DefaultSpriteBatch implements SpriteBatch {

    private final TexturedQuads quads;

    @Override
    public int width() {
        return quads.width();
    }

    @Override
    public int height() {
        return quads.height();
    }

    @Override
    public int drawCount() {
        return quads.drawCount();
    }

    /**
     * @param quads the textured quads to use
     */
    public DefaultSpriteBatch(TexturedQuads quads) {
        this.quads = requireNonNull(quads);
    }

    /**
     * @param program the program to use
     */
    public DefaultSpriteBatch(Wrap<ProgramObject> program) {
        this(new TexturedQuads(program));
    }

    @Override
    public void close() {
        quads.close();
    }

    @Override
    public void begin(int x, int y, int width, int height, boolean enableAlphaBlending) {
        quads.begin(x, y, width, height, enableAlphaBlending);
    }

    @Override
    public int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, int color) {
        return draw(font, x, y, maxWidth, text, TextAlignment.LEFT, color);
    }

    @Override
    public int draw(SpriteFont font, int x, int y, int maxWidth, CharSequence text, TextAlignment alignment, int color) {
        quads.use(font.texture());

        final LineStart lineStart = LineStart.from(alignment);
        final int dy = font.fontHeight() + font.glyphYBorder();
        final int maxX = x + maxWidth;
        int fx = lineStart.calculate(x, font, text, 0, maxWidth);
        int fy = y - dy;
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
                quads.addQuad(fx, fy, glyph.s0(), glyph.t1(), x1, y1, glyph.s1(), glyph.t0(), color);
            }

            fx = x1;
        }
        return y - fy;
    }

    @Override
    public void draw(Texture2d texture, int x, int y, int width, int height, int color) {
        draw(texture, x, y, width, height, 0f, 0f, 1f, 1f, color);
    }

    @Override
    public void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color) {
        quads.use(texture);
        quads.addQuad(x, y, s0, t0, x + width, y + height, s1, t1, color);
    }

    @Override
    public void end() {
        quads.end();
    }
}
