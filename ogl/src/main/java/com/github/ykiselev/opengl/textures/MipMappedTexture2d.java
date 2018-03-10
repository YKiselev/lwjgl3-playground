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

package com.github.ykiselev.opengl.textures;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;

/**
 * Mip-mapped texture
 * <p>
 * Created by Y.Kiselev on 05.06.2016.
 */
public final class MipMappedTexture2d implements Texture2d {

    private final int id;

    private final Consumer<Texture2d> onClose;

    private MipMappedTexture2d(int id, Consumer<Texture2d> onClose) {
        this.id = id;
        this.onClose = requireNonNull(onClose);
    }

    public MipMappedTexture2d(int id) {
        this(id, t -> glDeleteTextures(t.id()));
    }

    public MipMappedTexture2d() {
        this(glGenTextures());
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void close() {
        onClose.accept(this);
    }

    @Override
    public Texture2d manage(Consumer<Texture2d> onClose) {
        return new MipMappedTexture2d(id, onClose);
    }
}
