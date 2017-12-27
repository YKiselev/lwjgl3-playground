package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.IndexedGeometrySource;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import com.github.ykiselev.opengl.vertices.VertexDefinition;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static java.util.Objects.deepEquals;
import static java.util.Objects.requireNonNull;
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

    //private final UniformVariable texUniform;

    private final UniformVariable mvpUniform;

    private final int mode;

    private final int count;

    public GenericIndexedGeometry(ProgramObject program, VertexDefinition vertexDefinition, IndexedGeometrySource geometrySource) {
        this.program = requireNonNull(program);
        this.mode = geometrySource.mode();
        this.count = geometrySource.indices().remaining() / Integer.BYTES;

        program.bind();
        mvpUniform = program.lookup("mvp");
        //texUniform = program.lookup("tex");

        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind();
        vertexDefinition.apply(vbo);
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

        final double sec = System.currentTimeMillis() / 1000.0;

        final float radius = 4;

        try (MemoryStack ms = MemoryStack.stackPush()) {
            final double beta = Math.toRadians(15.0 * sec % 360);

            final FloatBuffer rm2 = ms.mallocFloat(16);
            //Matrix.lookAt(new Vector3f(0, 0, 0), new Vector3f((float) Math.sin(alpha), (float) Math.cos(alpha), 0), 0, rm2);
            final float x = (float) (Math.sin(beta) * radius);
            final float z = (float) (Math.cos(beta) * radius);
            Matrix.lookAt(
                    new Vector3f(0, 0, 0),
                    new Vector3f(x, z, -0.5f),
                    new Vector3f(0, 0, 1),
                    rm2
            );

            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(Math.toRadians(0), Math.toRadians(0), Math.toRadians(10*sec%360), rm);
            //Matrix.rotation(Math.toRadians(-90), Math.toRadians(0), Math.toRadians(sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);

            // t * r * p = M
            Matrix.identity(mvp);
            Matrix.translate(mvp, 1, 0, 0, mvp);
            Matrix.multiply(rm2, mvp, mvp);
            Matrix.multiply(mvp, rm, mvp);
            Matrix.multiply(projection, mvp, mvp);

            // M = p * t * r
//            Matrix.copy(projection, mvp);
//            Matrix.translate(mvp, 1, 0, -5f, mvp);
//            Matrix.multiply(mvp, rm, mvp);

            mvpUniform.matrix4(false, mvp);
        }

        //texUniform.value(0);

        vbo.bind();
        glDrawElements(mode, count, GL_UNSIGNED_INT, 0);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
        program.unbind();
    }
}
