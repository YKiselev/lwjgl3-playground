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

import com.github.ykiselev.opengl.Identified;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class ShaderObject implements Identified, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int id;

    @Override
    public int id() {
        return id;
    }

    public ShaderObject(int id) {
        if (id == 0) {
            throw new IllegalArgumentException("Zero is not a valid shader id!");
        }
        this.id = id;
    }

    public ShaderObject(int type, String source) throws ShaderException {
        this(glCreateShader(type));
        // todo ? Util.checkGLError();
        glShaderSource(id, source);
        // todo ? Util.checkGLError();
        glCompileShader(id);
        // todo ? Util.checkGLError();
        final int status = glGetShaderi(id, GL_COMPILE_STATUS);
        final String log = glGetShaderInfoLog(id, 8 * 1024);
        if (status != GL_TRUE) {
            throw new ShaderException(log);
        } else {
            if (StringUtils.isNotEmpty(log)) {
                logger.warn("Shader log: {}", log);
            }
        }
    }

    public ShaderObject(int type, URL resource, Charset charset) throws ShaderException, IOException {
        this(type, IOUtils.toString(resource, charset));
    }

    public ShaderObject(int type, URL resource) throws ShaderException, IOException {
        this(type, resource, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        glDeleteShader(id);
    }
}
