package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.IndexedGeometrySource;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

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

    private final FloatBuffer matrix;

    private final int mode;

    private final int count;

    public GenericIndexedGeometry(ProgramObject program, IndexedGeometrySource geometrySource) {
        this.program = requireNonNull(program);
        this.mode = geometrySource.mode();
        this.count = geometrySource.indices().remaining();

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

        matrix = MemoryUtil.memAllocFloat(16);
        Matrix.orthographic(0, 500, 500, 0, -10, 10, matrix);
    }

    @Override
    public void close() {
        vbo.close();
        ebo.close();
        vao.close();
        MemoryUtil.memFree(matrix);
    }

    public void draw() {
        vao.bind();
        program.bind();

        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer tm = ms.mallocFloat(16);
            Matrix.identity(tm);
            Matrix.translate(tm, 0, -2, -9, tm);

            final FloatBuffer pm = ms.mallocFloat(16);
            Matrix.perspective(-1, 1, 1, -1, 0.5f, 10, pm);

            Matrix.multiply(tm, pm, matrix);
        }
        //Matrix.identity(matrix);
        //Matrix.perspective(-1, 1, 1, -1, 0.5f, 10, matrix);
        //Matrix.translate(matrix, 0, 0, 0, matrix);

        texUniform.value(0);
        mvpUniform.matrix4(false, matrix);

        vbo.bind();
        glDrawElements(mode, count, GL_UNSIGNED_INT, 0);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();
    }
}
