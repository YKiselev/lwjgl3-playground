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

import com.github.ykiselev.lifetime.Manageable;
import com.github.ykiselev.opengl.Identified;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL20.glDeleteShader;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class ShaderObject implements Identified, AutoCloseable, Manageable<ShaderObject> {

    private final int id;

    private final Consumer<ShaderObject> onClose;

    @Override
    public int id() {
        return id;
    }

    public ShaderObject(int id, Consumer<ShaderObject> onClose) {
        if (id == 0) {
            throw new IllegalArgumentException("Zero is not a valid shader id!");
        }
        this.id = id;
        this.onClose = requireNonNull(onClose);
    }

    public ShaderObject(int id) {
        this(id, so -> glDeleteShader(so.id()));
    }

    @Override
    public ShaderObject manage(Consumer<ShaderObject> onClose) {
        return new ShaderObject(id, onClose);
    }

    @Override
    public void close() {
        onClose.accept(this);
    }
}
