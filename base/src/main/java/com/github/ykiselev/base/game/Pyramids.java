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

package com.github.ykiselev.base.game;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.models.GenericIndexedGeometry;
import com.github.ykiselev.opengl.models.Pyramid;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vertices.VertexDefinitions;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Pyramids implements AutoCloseable {

    private final GenericIndexedGeometry geometry;

    private final Wrap<ProgramObject> program;

    private final UniformVariable mvpUniform;

    public Pyramids(Assets assets) {
        program = assets.load("progs/colored.conf", ProgramObject.class);
        try (Pyramid p = new Pyramid()) {
            geometry = new GenericIndexedGeometry(VertexDefinitions.POSITION_COLOR, p);
        }
        mvpUniform = program.value().lookup("mvp");
    }

    public void draw(FloatBuffer vp) {
        geometry.begin();
        program.value().bind();
        final double sec = glfwGetTime();
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(25 * sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);

            // 1
            Matrix.multiply(vp, rm, mvp);
            mvpUniform.matrix4(false, mvp);
            geometry.draw();

            // 2
            Matrix.translate(vp, 2, 0, 0, mvp);
            Matrix.scale(mvp, 3, 3, 3, mvp);
            Matrix.multiply(mvp, rm, mvp);
            mvpUniform.matrix4(false, mvp);
            geometry.draw();

            // 3
            Matrix.translate(vp, -2, 0, 0, mvp);
            Matrix.multiply(mvp, rm, mvp);
            mvpUniform.matrix4(false, mvp);
            geometry.draw();
        }
        program.value().unbind();
        geometry.end();
    }

    @Override
    public void close() {
        geometry.close();
        program.close();
    }
}
