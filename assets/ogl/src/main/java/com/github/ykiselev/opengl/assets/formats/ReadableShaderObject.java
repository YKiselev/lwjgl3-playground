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

package com.github.ykiselev.opengl.assets.formats;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.common.io.ByteChannelAsString;
import com.github.ykiselev.opengl.shaders.DefaultShaderObject;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public final class ReadableShaderObject implements ReadableAsset<ShaderObject, Void> {

    private static final int MAX_SHADER_LOG_LENGTH = 8 * 1024;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int type;

    public ReadableShaderObject(int type) {
        this.type = type;
    }

    @Override
    public Wrap<ShaderObject> read(ReadableByteChannel channel, Recipe<ShaderObject, Void> recipe, Assets assets) throws ResourceException {
        final int id = glCreateShader(type);
        glShaderSource(id, new ByteChannelAsString(channel, StandardCharsets.UTF_8).read());
        glCompileShader(id);
        final int status = glGetShaderi(id, GL_COMPILE_STATUS);
        final String log = glGetShaderInfoLog(id, MAX_SHADER_LOG_LENGTH);
        if (status != GL_TRUE) {
            throw new ResourceException(log);
        } else {
            if (StringUtils.isNotEmpty(log)) {
                logger.warn("Shader log: {}", log);
            }
        }
        return Wraps.of(new DefaultShaderObject(id));
    }
}
