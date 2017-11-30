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
import com.github.ykiselev.opengl.shaders.ShaderException;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public class ReadableShaderObject implements ReadableResource<ShaderObject> {

    @Override
    public ShaderObject read(URI resource, Assets assets) throws ResourceException {
        final String text;
        try (InputStream is = assets.open(resource)) {
            text = IOUtils.toString(is, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new ResourceException(e);
        }
        try {
            return new ShaderObject(resolveType(resource), text);
        } catch (ShaderException e) {
            throw new ResourceException(e);
        }
    }

    private int resolveType(URI resource) {
        final String path = resource.getPath();
        if (StringUtils.endsWithIgnoreCase(path, ".frag")) {
            return GL20.GL_FRAGMENT_SHADER;
        } else if (StringUtils.endsWithIgnoreCase(path, ".vert")) {
            return GL20.GL_VERTEX_SHADER;
        }
        throw new IllegalArgumentException("Unknown shader type: " + resource);
    }
}
