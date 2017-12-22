package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.IndexedGeometrySource;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GenericIndexedGeometry implements AutoCloseable {

    private final ProgramObject program;

    private final VertexArrayObject vao;

    private final VertexBufferObject vbo;

    private final IndexBufferObject ebo;

    private final UniformVariable texUniform;

    private final UniformVariable mvpUniform;

    private final int mode;

    private final int count;

    public GenericIndexedGeometry(ProgramObject program, IndexedGeometrySource geometrySource) {
        this.program = requireNonNull(program);
        this.mode = geometrySource.mode();
        this.count = geometrySource.indices().remaining() / Integer.BYTES;

        program.bind();
        mvpUniform = program.lookup("mvp");
        texUniform = program.lookup("tex");

        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind();
        final int stride = (3 + 2 + 3) * Float.BYTES;
        vbo.attribute(
                program.attributeLocation("in_Position"), 3, GL_FLOAT, false, stride, 0
        );
        vbo.attribute(
                program.attributeLocation("in_TexCoord"), 2, GL_FLOAT, false, stride, (Float.BYTES * 3)
        );
        vbo.attribute(
                program.attributeLocation("in_Normal"), 3, GL_FLOAT, false, stride, (Float.BYTES * (3 + 2))
        );
        glBufferData(GL_ARRAY_BUFFER, geometrySource.vertices(), GL_STATIC_DRAW);

        ebo = new IndexBufferObject();
        ebo.bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, geometrySource.indices(), GL_STATIC_DRAW);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();
    }

    @Override
    public void close() {
        vbo.close();
        ebo.close();
        vao.close();
    }

    public void draw(FloatBuffer projection) {
        vao.bind();
        program.bind();

        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer tm = ms.mallocFloat(16);
            Matrix.identity(tm);
            Matrix.translate(tm, 0, 0, -1, tm);

            final long sec = System.currentTimeMillis() / 100;
            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(sec % 360), rm);

            Matrix.multiply(tm, rm, tm);

            final FloatBuffer mvp = ms.mallocFloat(16);
            Matrix.multiply(tm, projection, mvp);

            mvpUniform.matrix4(false, mvp);
        }

        texUniform.value(0);

        vbo.bind();
        glDrawElements(mode, count, GL_UNSIGNED_INT, 0);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();
    }
}
