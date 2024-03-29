/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.opengl.models;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.common.memory.MemAllocFloat;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import com.github.ykiselev.opengl.vertices.VaoConfigurer;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;


/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Block implements AutoCloseable {

    private static final float[] VERTICES = new float[]{
            // x,y,z,s,t,nx,ny,nz
            // xz
            0, 0, 0, 0, 0, 0, -1, 0,
            1, 0, 0, 0.3333f, 0, 0, -1, 0,
            1, 0, 1, 0.3333f, 1, 0, -1, 0,
            0, 0, 1, 0, 1, 0, -1, 0,
            // yz
            0, 0, 0, 0.3333f, 0, -1, 0, 0,
            0, 0, 1, 0.3333f, 1, -1, 0, 0,
            0, 1, 1, 0, 1, -1, 0, 0,
            0, 1, 0, 0, 0, -1, 0, 0,
            // xz+1
            0, 1, 0, 0.3333f, 0, 0, 1, 0,
            0, 1, 1, 0.3333f, 1, 0, 1, 0,
            1, 1, 1, 0, 1, 0, 1, 0,
            1, 1, 0, 0, 0, 0, 1, 0,
            // yz+1
            1, 0, 0, 0, 0, 1, 0, 0,
            1, 1, 0, 0.3333f, 0, 1, 0, 0,
            1, 1, 1, 0.3333f, 1, 1, 0, 0,
            1, 0, 1, 0, 1, 1, 0, 0,
            // xy
            0, 0, 0, 0.3333f, 0, 0, 0, -1,
            0, 1, 0, 0.6666f, 0, 0, 0, -1,
            1, 1, 0, 0.6666f, 1, 0, 0, -1,
            1, 0, 0, 0.3333f, 1, 0, 0, -1,
            // xy+1
            0, 0, 1, 0.3333f, 1, 0, 0, 1,
            1, 0, 1, 0.3333f, 0, 0, 0, 1,
            1, 1, 1, 0.6666f, 0, 0, 0, 1,
            0, 1, 1, 0.6666f, 1, 0, 0, 1
    };

    private static final int[] INDICES = new int[]{
            // xz
            0, 1, 2,
            2, 3, 0,
            // yz
            4, 5, 6,
            6, 7, 4,
            // xz+1
            8, 9, 10,
            10, 11, 8,
            // yz+1
            12, 13, 14,
            14, 15, 12,
            // xy
            16, 17, 18,
            18, 19, 16,
            // xy+1
            20, 21, 22,
            22, 23, 20
    };
    public static final int FLOATS_PER_INSTANCE = 5;

    private final ProgramObject program;

    private final VertexArrayObject vao;

    private final VertexBufferObject dynamicVbo;

    private final UniformVariable mvp, texScale, tex;

    private final FloatBuffer buffer;

    private int instances;

    private long totalInstances;

    private final AutoCloseable ac;

    public long totalInstances() {
        return totalInstances;
    }

    public Block(Assets assets, int instanceLimit) {
        try (var guard = Closeables.newGuard()) {
            program = guard.add(assets.load("progs/block.conf", OglRecipes.PROGRAM));
            vao = guard.add(new VertexArrayObject());
            var staticVbo = guard.add(new VertexBufferObject());
            dynamicVbo = guard.add(new VertexBufferObject());
            var ebo = guard.add(new IndexBufferObject());

            VaoConfigurer.of(vao)
                    .with(staticVbo)
                    .floats(3) // position
                    .floats(2) // tex coords
                    .floats(3) // normal
                    .endVbo()
                    .with(dynamicVbo)
                    .floats(3, 1) // block pos
                    .floats(2, 1) // tex offset
                    .endVbo()
                    .with(ebo)
                    .end();

            try (var ms = MemoryStack.stackPush()) {
                FloatBuffer buf = ms.mallocFloat(VERTICES.length);
                buf.clear().put(VERTICES).flip();
                staticVbo.bind();
                staticVbo.bufferData(buf, GL_STATIC_DRAW);
                staticVbo.unbind();

                IntBuffer ibuf = ms.mallocInt(INDICES.length);
                ibuf.clear().put(INDICES).flip();
                ebo.bind();
                ebo.bufferData(ibuf, GL_STATIC_DRAW);
                ebo.unbind();
            }

            buffer = guard.add(new MemAllocFloat(FLOATS_PER_INSTANCE * instanceLimit));
            mvp = program.lookup("mvp");
            texScale = program.lookup("texScale");
            tex = program.lookup("tex");

            ac = guard.detach();
        }
    }

    public void begin(FloatBuffer m, float sScale, float tScale) {
        totalInstances = 0;
        program.bind();
        vao.bind();
        tex.value(0);

        mvp.matrix4(false, m);
        try (var ms = MemoryStack.stackPush()) {
            FloatBuffer buf = ms.mallocFloat(2);
            buf.clear().put(sScale).put(tScale).flip();
            texScale.vector2(buf);
        }
    }

    public void end() {
        flush();

        vao.unbind();
        program.unbind();
    }

    private void flush() {
        if (instances > 0) {
            dynamicVbo.bind();
            dynamicVbo.bufferData(buffer.flip(), GL_DYNAMIC_DRAW);
            dynamicVbo.unbind();
            GL31.glDrawElementsInstanced(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0, instances);
            totalInstances += instances;
            instances = 0;
            buffer.clear();
        }
    }

    public void draw(float x, float y, float z, float sOff, float tOff) {
        if (buffer.remaining() < FLOATS_PER_INSTANCE) {
            flush();
        }
        buffer.put(x).put(y).put(z).put(sOff).put(tOff);
        instances++;
    }

    @Override
    public void close() {
        Closeables.close(ac);
    }
}
