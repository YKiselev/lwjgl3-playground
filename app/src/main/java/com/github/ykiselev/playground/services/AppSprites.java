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

package com.github.ykiselev.playground.services;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.textures.Sprite;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.layers.Sprites;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSprites implements Sprites, AutoCloseable {

    private Services services;

    public AppSprites(Services services) {
        this.services = requireNonNull(services);
    }

    @Override
    public SpriteBatch newBatch() {
        final Assets assets = services.resolve(Assets.class);
        return new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class),
                assets.load("images/white.png", Sprite.class)
        );
    }

    @Override
    public void close() {
    }
}
