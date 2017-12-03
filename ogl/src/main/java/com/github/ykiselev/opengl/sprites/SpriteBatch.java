package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Created by Uze on 17.01.2015.
 */
public final class SpriteBatch implements AutoCloseable {

    private static final float COLOR_COEFF = 1.0f / 255.0f;

    private static final int MAX_QUADS = 32;

    private static final int VERTEX_SIZE_IN_FLOATS = 4;

    private static final int MAX_VERTICES = MAX_QUADS * 4 * VERTEX_SIZE_IN_FLOATS;

    private static final int MAX_INDICES = MAX_QUADS * 6;

    private final ProgramObject program;

    private final FloatBuffer vertices;

    private final FloatBuffer colors;

    private final VertexBufferObject vbo;

    private final VertexArrayObject vao;

    private final IndexBufferObject ebo;

    private final UniformVariable texUniform;

    private final FloatBuffer matrix;

    private final UniformVariable mvpUniform;

    private final UniformVariable colorsUniform;

    private Texture2d currentTexture;

    private int quadCounter;

    private int width;

    private int height;

    private int drawCount;

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int drawCount() {
        return drawCount;
    }

    public SpriteBatch(ProgramObject program) {
        this.program = requireNonNull(program);

        vao = new VertexArrayObject();
        vao.bind();

        //todo program.bindFragDataLocation(0, "outColor");
        program.bind();

        mvpUniform = program.lookup("mvp");
        colorsUniform = program.lookup("colors");

        vbo = new VertexBufferObject();
        vbo.bind();

        vertices = BufferUtils.createFloatBuffer(MAX_VERTICES);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        texUniform = program.lookup("tex");
        vbo.attribute(
                program.attributeLocation("in_Position"), 2, GL_FLOAT, false, 16, 0
        );
        vbo.attribute(
                program.attributeLocation("in_TexCoord"), 2, GL_FLOAT, false, 16, 8
        );

        ebo = new IndexBufferObject();
        ebo.bind();

        // Quad == 2 triangles == 6 indices (can't use stripes due to texture coordinates difference between quads)
        final IntBuffer indices = BufferUtils.createIntBuffer(MAX_INDICES);
        int offset = 0;
        for (int i = 0; i < MAX_QUADS; i++) {
            indices.put(offset);
            indices.put(offset + 1);
            indices.put(offset + 2);
            indices.put(offset + 2);
            indices.put(offset + 1);
            indices.put(offset + 3);
            offset += 4;
        }
        indices.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
        vbo.unbind();
        ebo.unbind();
        program.unbind();

        matrix = MemoryUtil.memAllocFloat(16);
        colors = MemoryUtil.memAllocFloat(MAX_QUADS * 4);
    }

    @Override
    public void close() {
        vbo.close();
        ebo.close();
        vao.close();
        MemoryUtil.memFree(matrix);
        MemoryUtil.memFree(colors);
    }

    private void flush() {
        if (quadCounter <= 0) {
            return;
        }
        vbo.bind();

        vertices.flip();
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        colors.flip();
        colorsUniform.vector4fv(colors);

        glDrawElements(GL_TRIANGLES, quadCounter * 6, GL_UNSIGNED_INT, 0);

        colors.clear();
        vertices.clear();
        quadCounter = 0;
        drawCount++;
    }

    /**
     * Add new quad (4 vertices) to queue
     * <pre>
     * (2) --- (3)
     *  |       |
     *  |       |
     * (0) --- (1)
     *
     * (x0, y1) --- (x1, y1)
     *  |                 |
     *  |                 |
     * (x0, y0) --- (x1, y0)
     * </pre>
     * <p>Note that if you wish to draw quad with whole texture applied
     * then s, t must be assigned as such:</p>
     * <pre>
     * (0, 1) --- (1, 1)
     *  |             |
     *  |             |
     * (0, 0) --- (1, 0)
     * </pre>
     * <p>Quad is rendered as two triangles with indices 0, 1, 2, 2, 1, 3</p>
     *
     * @param color the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     */
    private void addQuad(float x0, float y0, float s0, float t0, float x1, float y1, float s1, float t1, int color) {
        if (quadCounter >= MAX_QUADS) {
            flush();
        }

        vertices.put(x0).put(y0).put(s0).put(t0);
        vertices.put(x1).put(y0).put(s1).put(t0);
        vertices.put(x0).put(y1).put(s0).put(t1);
        vertices.put(x1).put(y1).put(s1).put(t1);

        // r
        colors.put(COLOR_COEFF * (0xff & (color >>> 24)));
        // g
        colors.put(COLOR_COEFF * (0xff & (color >>> 16)));
        // b
        colors.put(COLOR_COEFF * (0xff & (color >>> 8)));
        // a
        colors.put(COLOR_COEFF * (0xff & color));

        quadCounter++;
    }

    private void use(Texture2d texture) {
        if (texture == null) {
            flush();
            glBindTexture(GL_TEXTURE_2D, 0);
        } else if (texture != currentTexture) {
            flush();
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
        }
        currentTexture = texture;
    }

    /**
     * @param x                   the left viewport coordinate
     * @param y                   the bottom viewport coordinate
     * @param width               the width of viewport
     * @param height              the height of viewport
     * @param enableAlphaBlending set to {@code true} to use alpha-blending
     */
    public void begin(int x, int y, int width, int height, boolean enableAlphaBlending) {
        this.width = width;
        this.height = height;

        glViewport(x, y, width, height);

        vao.bind();
        program.bind();
        use(null);

        if (enableAlphaBlending) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        } else {
            glDisable(GL_BLEND);
        }

        texUniform.value1i(0);

        final float oow = 1.0f / (float) width;
        final float ooh = 1.0f / (float) height;
        final float scaleX = 2.0f * oow;
        final float scaleY = 2.0f * ooh;
        final float transX = -1;
        final float transY = -1;

        matrix.clear();
        matrix.put(scaleX).put(0).put(0).put(0);
        matrix.put(0).put(scaleY).put(0).put(0);
        matrix.put(0).put(0).put(1).put(0);
        matrix.put(transX).put(transY).put(0).put(1);
        matrix.flip();
        mvpUniform.matrix4(false, matrix);

        drawCount = 0;
    }

    /**
     * Draws text at specified location with specified sprite font, maximum width and color.
     * </p>
     *
     * @param font     the sprite font to use
     * @param x        the left coordinate of the origin of the text bounding rectangle
     * @param y        the bottom coordinate of the origin of the text bounding rectangle
     * @param text     the text to draw (possibly multi-line if there is '\n' characters in text or if maxWidth exceeded)
     * @param maxWidth the maximum width of bounding rectangle. When text width reaches this value next character is drawn as if there '\n' between next and previous characters.
     * @param color    the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff)
     */
    public void draw(SpriteFont font, int x, int y, String text, int maxWidth, int color) {
        use(font.texture());

        final float dy = font.fontHeight() + font.glyphYBorder();
        float maxX = x + maxWidth;
        float fx = x;
        float fy = y;
        for (int i = 0; i < text.length(); i++) {
            final char value = text.charAt(i);

            if (value == '\r') {
                continue;
            }
            if (value == '\n') {
                fx = x;
                fy -= dy;
                continue;
            }

            final Glyph glyph = font.glyphForCharacter(value);

            if (fx + glyph.getWidth() > maxX) {
                fx = x;
                fy -= dy;
            }

            final float x1 = fx + glyph.getWidth();
            final float y1 = fy + font.fontHeight();

            if (value != ' ') {
                addQuad(fx, fy, glyph.getS0(), glyph.getT0(), x1, y1, glyph.getS1(), glyph.getT1(), color);
            }

            fx = x1;
        }
    }

    public void draw(Texture2d texture, int x, int y, int width, int height, int color) {
        use(texture);
        addQuad(x, y, 0f, 0f, x + width, y + height, 1f, 1f, color);
    }

    public void draw(Texture2d texture, int x, int y, int width, int height, float s0, float t0, float s1, float t1, int color) {
        use(texture);
        addQuad(x, y, s0, t0, x + width, y + height, s1, t1, color);
    }

    public void end() {
        flush();
        use(null);
        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        glDisable(GL_BLEND);
    }
}
