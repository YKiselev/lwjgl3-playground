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
import com.github.ykiselev.opengl.shaders.ProgramException;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.ViewShader;

import java.net.URI;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by Y.Kiselev on 15.06.2016.
 */
public final class ReadableViewShader implements ReadableResource<ViewShader> {

    @Override
    public ViewShader read(ReadableByteChannel channel, URI resource, Assets assets) throws ResourceException {
        try {
            return new ViewShader(
                    assets.resolve(ProgramObject.class)
                            .read(channel, resource, assets)
            );
        } catch (ProgramException e) {
            throw new ResourceException("Failed to load " + resource, e);
        }
    }
}
