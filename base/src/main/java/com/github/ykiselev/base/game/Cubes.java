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
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.opengl.models.GenericIndexedGeometry;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.vertices.VertexDefinitions;
import com.github.ykiselev.wrap.Wrap;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Cubes implements AutoCloseable {

    private final GenericIndexedGeometry geometry;

    private final UniformVariable texUniform;

    private final UniformVariable mvpUniform;

    private final Wrap<ProgramObject> program;

    public Cubes(Assets assets) {
        program = assets.load("progs/generic.conf", ProgramObject.class);
        try (Wrap<ObjModel> model = assets.load("models/2cubes.obj", ObjModel.class)) {
            geometry = new GenericIndexedGeometry(
                    VertexDefinitions.POSITION_TEXTURE_NORMAL,
                    model.value().toIndexedTriangles()
            );
        }
        final ProgramObject prg = program.value();
        mvpUniform = prg.lookup("mvp");
        texUniform = prg.lookup("tex");
    }

    public void draw(FloatBuffer mvp) {
        geometry.begin();
        program.value().bind();
        texUniform.value(0);
        mvpUniform.matrix4(false, mvp);
        geometry.draw();
        geometry.end();
        program.value().unbind();
    }

    @Override
    public void close() {
        geometry.close();
        program.close();
    }
}
