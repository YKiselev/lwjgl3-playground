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

package com.github.ykiselev.opengl.shaders;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.shaders.uniforms.Matrix4Variable;

/**
 * Created by Y.Kiselev on 12.05.2016.
 */
public final class ViewShader implements Bindable {

    private final ProgramObject program;

    private final Matrix4Variable projectionMatrix;

    private final Matrix4Variable viewMatrix;

    private final Matrix4Variable modelMatrix;

    public ViewShader(ProgramObject program, Matrix4Variable projectionMatrix, Matrix4Variable viewMatrix,
                      Matrix4Variable modelMatrix) {
        this.program = program;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.modelMatrix = modelMatrix;
    }

    public ViewShader(ProgramObject program) throws ShaderException {
        this(
                program,
                new Matrix4Variable(program.lookup("projectionMatrix")),
                new Matrix4Variable(program.lookup("viewMatrix")),
                new Matrix4Variable(program.lookup("modelMatrix"))
        );
    }

    @Override
    public void bind() {
        this.program.bind();
    }

    @Override
    public void unbind() {
        this.program.unbind();
    }

    @Override
    public int id() {
        return program.id();
    }

    public Matrix4Variable projectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4Variable viewMatrix() {
        return viewMatrix;
    }

    public Matrix4Variable modelMatrix() {
        return modelMatrix;
    }
}
