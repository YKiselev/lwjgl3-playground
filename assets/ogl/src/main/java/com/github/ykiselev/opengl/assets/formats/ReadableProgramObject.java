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
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.shaders.DefaultProgramObject;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import com.github.ykiselev.playground.assets.common.AssetUtils;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ReadableByteChannel;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1i;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class ReadableProgramObject implements ReadableAsset<ProgramObject, Void> {

    private static final int MAX_PROGRAM_LOG_LENGTH = 8 * 1024;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Wrap<ProgramObject> read(ReadableByteChannel channel, Recipe<ProgramObject, Void> recipe, Assets assets) throws ResourceException {
        try (Wrap<Config> fallback = assets.load("progs/default/program-object.conf", OglRecipes.CONFIG);
             Wrap<Config> cfg = AssetUtils.read(channel, OglRecipes.CONFIG, assets)) {
            final Config config = cfg.value()
                    .withFallback(fallback.value());

            final int id = glCreateProgram();
            final Wrap<ShaderObject>[] shaders = readShaders(assets, config);
            for (Wrap<ShaderObject> w : shaders) {
                glAttachShader(id, w.value().id());
            }
            final List<String> locations = config.getStringList("vertex-attribute-locations");
            int i = 0;
            for (String location : locations) {
                glBindAttribLocation(id, i, location);
                i++;
            }
            glLinkProgram(id);
            final String log = glGetProgramInfoLog(id, MAX_PROGRAM_LOG_LENGTH);
            final int status = glGetProgrami(id, GL_LINK_STATUS);
            if (status != GL_TRUE) {
                throw new ResourceException(log);
            } else if (StringUtils.isNotEmpty(log)) {
                logger.warn("Program link log: {}", log);
            }
            final ProgramObject program = new DefaultProgramObject(id, shaders);
            final List<String> samplers = config.getStringList("samplers");
            if (!samplers.isEmpty()) {
                program.bind();
                int unit = 0;
                for (String uniform : samplers) {
                    glUniform1i(program.uniformLocation(uniform), unit);
                    unit++;
                }
                program.unbind();
            }
            return Wraps.of(program);
        }
    }

    @SuppressWarnings("unchecked")
    private Wrap<ShaderObject>[] readShaders(Assets assets, Config config) {
        return config.getConfig("shaders").root()
                .values()
                .stream()
                .map(ConfigValue::unwrapped)
                .map(String.class::cast)
                .filter(v -> !v.isEmpty())
                .map(uri -> assets.load(uri, null))
                .toArray(Wrap[]::new);
    }
}
