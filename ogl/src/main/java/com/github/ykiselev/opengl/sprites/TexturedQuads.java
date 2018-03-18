package com.github.ykiselev.opengl.sprites;

import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.shaders.DefaultProgramObject;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import com.github.ykiselev.opengl.vertices.VertexDefinitions;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.IntConsumer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;

/**
 * Created by Uze on 17.01.2015.
 */
public final class TexturedQuads implements AutoCloseable {

    private static final float COLOR_COEFF = 1.0f / 255.0f;

    // Note: fragment shader depends on this number through colors uniform.
    private static final int MAX_QUADS = 32;

    private static final int VERTEX_SIZE_IN_FLOATS = 4;

    private static final int MAX_VERTICES = MAX_QUADS * 4 * VERTEX_SIZE_IN_FLOATS;

    private static final int MAX_INDICES = MAX_QUADS * 6;

    private static final int INDEX_VALUE_TYPE = MAX_INDICES < 0xff
            ? GL_UNSIGNED_BYTE
            : (MAX_INDICES < 0xffff ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT);

    private final ProgramObject program;

    private final FloatBuffer vertices;

    private final FloatBuffer colors;

    private final VertexBufferObject vbo;

    private final VertexArrayObject vao;

    private final IndexBufferObject ebo;

    private final UniformVariable texUniform;

    private final UniformVariable mvpUniform;

    private final UniformVariable colorsUniform;

    private final FloatBuffer matrix;

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

    /**
     * Note that this class doesn't take ownership over supplied program and hence won't call {@link DefaultProgramObject#close()} method on close!
     *
     * @param program the program to use
     */
    public TexturedQuads(ProgramObject program) {
        this.program = requireNonNull(program);

        vertices = BufferUtils.createFloatBuffer(MAX_VERTICES);

        program.bind();
        mvpUniform = program.lookup("mvp");
        colorsUniform = program.lookup("colors");
        texUniform = program.lookup("tex");

        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind();
        VertexDefinitions.POSITION2_TEXTURE2.apply(vbo);

        ebo = new IndexBufferObject();
        ebo.bind();

        // Quad == 2 triangles == 6 indices (can't use stripes due to texture coordinates difference between quads)
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final ByteBuffer indices = ms.malloc(4, 4 * MAX_INDICES);
            fillIndexData(indices);
            indices.flip();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();

        matrix = MemoryUtil.memAllocFloat(16);
        colors = MemoryUtil.memAllocFloat(MAX_QUADS * 4);
    }

    private void fillIndexData(ByteBuffer b) {
        final IntConsumer c;
        switch (INDEX_VALUE_TYPE) {
            case GL_UNSIGNED_BYTE:
                c = v -> b.put((byte) v);
                break;

            case GL_UNSIGNED_SHORT:
                c = v -> b.putShort((short) v);
                break;

            case GL_UNSIGNED_INT:
                c = b::putInt;
                break;

            default:
                throw new IllegalArgumentException("Bad index value type: " + INDEX_VALUE_TYPE);
        }
        int offset = 0;
        for (int i = 0; i < MAX_QUADS; i++) {
            c.accept(offset);
            c.accept(offset + 1);
            c.accept(offset + 2);
            c.accept(offset + 2);
            c.accept(offset + 1);
            c.accept(offset + 3);
            offset += 4;
        }
    }

    @Override
    public void close() throws Exception {
        vbo.close();
        ebo.close();
        vao.close();
        program.close();
        MemoryUtil.memFree(matrix);
        MemoryUtil.memFree(colors);
    }

    private void flush() {
        if (quadCounter <= 0) {
            return;
        }
        vbo.bind();
        vertices.flip();
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);

        colors.flip();
        colorsUniform.vector4(colors);

        glDrawElements(GL_TRIANGLES, quadCounter * 6, INDEX_VALUE_TYPE, 0);

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
     *
     * (s0, t1) --- (s1, t1)
     *  |                 |
     *  |                 |
     * (s0, t0) --- (s1, t0)
     *
     * </pre>
     * <p>Note that if you wish to draw quad with texture loaded from external resource then s, t must be assigned as such:</p>
     * <pre>
     * (0, 0) --- (1, 0)
     *  |             |
     *  |             |
     * (0, 1) --- (1, 1)
     * </pre>
     * That is because OpenGL texture origin (0,0) is in lower left corner while most of the image formats have origin in the left top corner.
     * This leads to images drawn upside-down. To re-mediate this we need to flip image vertically either
     * <ol>
     * <li>During resource preparation step</li>
     * <li>At run-time before actually submitting image to OpenGL</li>
     * <li>By flipping texture t-coordinate (cheapest solution, but leads to increased api complexity)</li>
     * </ol>
     * <p>Quad is rendered as two triangles with indices 0, 1, 2, 2, 1, 3</p>
     *
     * @param color the RGBA color (0xff0000ff - red, 0x00ff00ff - green, 0x0000ffff - blue)
     */
    public void addQuad(float x0, float y0, float s0, float t0, float x1, float y1, float s1, float t1, int color) {
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

    public void use(Texture2d texture) {
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

        texUniform.value(0);

        Matrix.orthographic(x, x + width, y + height, y, -1, 1, matrix);
        mvpUniform.matrix4(false, matrix);

        drawCount = 0;
    }

    public void end() {
        flush();
        use(null);
        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();
        glDisable(GL_BLEND);
    }
}