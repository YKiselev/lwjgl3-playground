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
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class ProgramObject implements Bindable, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int id;

    private final ShaderObject[] shaders;

    @Override
    public int id() {
        return id;
    }

    public ProgramObject(Collection<VertexAttributeLocation> locations, Collection<String> samplers, ShaderObject... shaders) throws ShaderException {
        this.shaders = shaders;
        this.id = glCreateProgram();
        //todo Util.checkGLError();
        for (ShaderObject s : shaders) {
            glAttachShader(id, s.id());
            //todo Util.checkGLError();
        }
        for (VertexAttributeLocation location : locations) {
            glBindAttribLocation(id, location.index(), location.name());
            //todo Util.checkGLError();
        }
        glLinkProgram(id);
        //todo Util.checkGLError();
        final String log = glGetProgramInfoLog(id, 8 * 1024);
        final int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new ShaderException(log);
        } else if (StringUtils.isNotEmpty(log)) {
            logger.warn("Program link log: {}", log);
        }
        if (!samplers.isEmpty()) {
            bind();
            int unit = 0;
            for (String sampler : samplers) {
                glUniform1i(location(sampler), unit);
                //todo Util.checkGLError();
                unit++;
            }
            unbind();
        }
    }

    @Override
    public void bind() {
        glUseProgram(id);
        //todo Util.checkGLError();
    }

    @Override
    public void unbind() {
        glUseProgram(0);
        //todo Util.checkGLError();
    }

    private int location(String uniform) throws ShaderException {
        final int location = glGetUniformLocation(this.id, uniform);
        if (location == -1) {
            throw new ShaderException("Uniform variable not found: " + uniform
                    + ".\nThis may be due to compiler optimization, check if variable is actually used in code!");
        }
        return location;
    }

    /**
     * @param uniform the name of the uniform variable
     * @return the new instance of uniform variable
     */
    public UniformVariable lookup(String uniform) throws ShaderException {
        return new UniformVariable(location(uniform), uniform);
    }

    @Override
    public void close() throws Exception {
        for (ShaderObject shader : shaders) {
            shader.close();
        }
        glDeleteProgram(id);
    }
}
