package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.IndexedGeometrySource;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GenericIndexedGeometry {

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
    }

    public void draw() {
        vao.bind();
        program.bind();
        texUniform.value(0);
        mvpUniform.matrix4(false, matrix);
        vbo.bind();
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();
    }
}
