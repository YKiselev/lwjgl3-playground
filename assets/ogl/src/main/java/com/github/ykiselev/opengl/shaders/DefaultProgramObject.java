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

import com.github.ykiselev.opengl.shaders.ProgramException.AttributeNotFoundException;
import com.github.ykiselev.opengl.shaders.ProgramException.UniformVariableNotFoundException;
import com.github.ykiselev.opengl.shaders.uniforms.UniformInfo;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
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
    public int uniformLocation(String uniform) throws UniformVariableNotFoundException {
        final int location = glGetUniformLocation(id, uniform);
        if (location == -1) {
            throw new UniformVariableNotFoundException(uniform);
        }
        return location;
    }

    @Override
    public int attributeLocation(String attribute) throws AttributeNotFoundException {
        final int location = glGetAttribLocation(id, attribute);
        if (location == -1) {
            throw new AttributeNotFoundException(attribute);
        }
        return location;
    }

    @Override
    public UniformVariable lookup(String uniform) throws UniformVariableNotFoundException {
        return new UniformVariable(uniformLocation(uniform), uniform);
    }

    @Override
    public UniformInfo describe(int location) throws ProgramException {
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final ByteBuffer nameBuf = ms.malloc(GL20.glGetProgrami(id(), GL_ACTIVE_UNIFORM_MAX_LENGTH));
            final IntBuffer sizeBuf = ms.mallocInt(1);
            final IntBuffer typeBuf = ms.mallocInt(1);
            GL20.glGetActiveUniform(id(), location, null, sizeBuf, typeBuf, nameBuf);
            return new UniformInfo(typeBuf.get(0), sizeBuf.get(0));
        }
    }

    @Override
    public void close() {
        for (Wrap<ShaderObject> shader : shaders) {
            glDetachShader(id, shader.value().id());
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
