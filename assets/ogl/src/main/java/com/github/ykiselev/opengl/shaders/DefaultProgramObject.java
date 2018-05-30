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

import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.wrap.Wrap;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class DefaultProgramObject implements ProgramObject {

    private final int id;

    private final Wrap<ShaderObject>[] shaders;

    @Override
    public int id() {
        return id;
    }

    public DefaultProgramObject(int id, Wrap<ShaderObject>[] shaders) {
        this.id = id;
        this.shaders = requireNonNull(shaders);
    }

    @Override
    public void bind() {
        glUseProgram(id);
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public int uniformLocation(String uniform) throws ProgramException {
        final int location = glGetUniformLocation(id, uniform);
        if (location == -1) {
            throw new ProgramException("Uniform variable not found: " + uniform
                    + ".\nThis may be caused by compiler optimization, check if variable is actually used in code!");
        }
        return location;
    }

    @Override
    public int attributeLocation(String attribute) throws ProgramException {
        final int location = glGetAttribLocation(id, attribute);
        if (location == -1) {
            throw new ProgramException("Attribute not found: " + attribute
                    + ".\nThis may be caused by compiler optimization, check if attribute is actually used in code!");
        }
        return location;
    }

    /**
     * @param uniform the name of the uniform variable
     * @return the new instance of uniform variable
     */
    @Override
    public UniformVariable lookup(String uniform) throws ProgramException {
        return new UniformVariable(uniformLocation(uniform), uniform);
    }

    @Override
    public void close() {
        for (Wrap<?> shader : shaders) {
            shader.close();
        }
        glDeleteProgram(id);
    }

    @Override
    public String toString() {
        return "DefaultProgramObject{" +
                "id=" + id +
                '}';
    }
}
