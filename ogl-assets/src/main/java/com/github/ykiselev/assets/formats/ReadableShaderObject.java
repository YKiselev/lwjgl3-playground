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

package com.github.ykiselev.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class ReadableShaderObject implements ReadableResource<ShaderObject> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ShaderObject read(ReadableByteChannel channel, URI resource, Assets assets) throws ResourceException {
        final String text;
        try (Reader reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8.newDecoder(), -1))) {
            text = IOUtils.toString(reader);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
        final int id = glCreateShader(
                resolveType(resource)
        );
        glShaderSource(id, text);
        glCompileShader(id);
        final int status = glGetShaderi(id, GL_COMPILE_STATUS);
        final String log = glGetShaderInfoLog(id, 8 * 1024);
        if (status != GL_TRUE) {
            throw new ResourceException(log);
        } else {
            if (StringUtils.isNotEmpty(log)) {
                logger.warn("Shader log: {}", log);
            }
        }
        return new ShaderObject(id);
    }

    private int resolveType(URI resource) {
        final String path = resource.getPath();
        if (StringUtils.endsWithIgnoreCase(path, ".fs")) {
            return GL20.GL_FRAGMENT_SHADER;
        } else if (StringUtils.endsWithIgnoreCase(path, ".vs")) {
            return GL20.GL_VERTEX_SHADER;
        }
        throw new IllegalArgumentException("Unknown shader type: " + resource);
    }
}
