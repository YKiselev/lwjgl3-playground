package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.IndexedGeometrySource;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import com.github.ykiselev.opengl.vertices.VertexDefinition;
import com.github.ykiselev.wrap.Wrap;

import java.nio.FloatBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GenericIndexedGeometry implements AutoCloseable {

    private final Wrap<ProgramObject> program;

    private final VertexArrayObject vao;

    private final VertexBufferObject vbo;

    private final IndexBufferObject ebo;

    private final UniformVariable mvpUniform;

    private final int mode;

    private final int count;

    public GenericIndexedGeometry(Wrap<ProgramObject> program, VertexDefinition vertexDefinition, IndexedGeometrySource geometrySource) {
        this.program = requireNonNull(program);
        this.mode = geometrySource.mode();
        this.count = geometrySource.indices().remaining() / Integer.BYTES;

        final ProgramObject prg = program.value();
        prg.bind();
        mvpUniform = prg.lookup("mvp");
        //texUniform = prg.lookup("tex");

        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind();
        vertexDefinition.apply(vbo);
        vbo.bufferData(geometrySource.vertices(), GL_STATIC_DRAW);

        ebo = new IndexBufferObject();
        ebo.bind();
        ebo.bufferData(geometrySource.indices(), GL_STATIC_DRAW);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        prg.unbind();
    }

    @Override
    public void close() {
        vbo.close();
        ebo.close();
        vao.close();
        program.close();
    }

    public void draw(FloatBuffer mvp) {
        vao.bind();
        program.value().bind();

        mvpUniform.matrix4(false, mvp);
        //texUniform.value(0);

        vbo.bind();
        glDrawElements(mode, count, GL_UNSIGNED_INT, 0);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.value().unbind();
    }
}
