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

package com.github.ykiselev.playground.services.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.CompositeReadableAssets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;
import com.github.ykiselev.assets.SimpleAssets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.openal.assets.ReadableVorbisAudio;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.assets.formats.ReadableConfig;
import com.github.ykiselev.opengl.assets.formats.ReadableFontAtlas;
import com.github.ykiselev.opengl.assets.formats.ReadableObjModel;
import com.github.ykiselev.opengl.assets.formats.ReadableProgramObject;
import com.github.ykiselev.opengl.assets.formats.ReadableShaderObject;
import com.github.ykiselev.opengl.assets.formats.ReadableSpriteFont;
import com.github.ykiselev.opengl.assets.formats.ReadableTexture2d;
import com.github.ykiselev.opengl.assets.formats.ReadableTrueTypeFontInfo;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.opengl.GL20;

import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameAssets implements Assets, AutoCloseable {

    private final Assets delegate;

    public GameAssets(Assets delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public <K, T, C> Wrap<T> tryLoad(String resource, Recipe<K, T, C> recipe, Assets assets) throws ResourceException {
        return delegate.tryLoad(resource, recipe, assets);
    }

    @Override
    public <K, T, C> ReadableAsset<T, C> resolve(String resource, Recipe<K, T, C> recipe) throws ResourceException {
        return delegate.resolve(resource, recipe);
    }

    @Override
    public void close() {
        Closeables.close(delegate);
    }

    @Override
    public Optional<ReadableByteChannel> open(String resource) throws ResourceException {
        return delegate.open(resource);
    }

    @Override
    public Stream<ReadableByteChannel> openAll(String resource) throws ResourceException {
        return delegate.openAll(resource);
    }

    public static Assets create(Resources resources) {
        final ReadableConfig readableConfig = new ReadableConfig();
        final ReadableTexture2d readableTexture2d = new ReadableTexture2d();
        final Map<Object, ReadableAsset> byKey = Map.of(
                OglRecipes.CONFIG.key(), readableConfig,
                OglRecipes.PROGRAM.key(), new ReadableProgramObject(),
                OglRecipes.SPRITE_FONT.key(), new ReadableSpriteFont(),
                OglRecipes.SPRITE.key(), readableTexture2d,
                OglRecipes.MIP_MAP_TEXTURE.key(), readableTexture2d,
                OglRecipes.OBJ_MODEL.key(), new ReadableObjModel(),
                OglRecipes.TRUE_TYPE_FONT_INFO.key(), new ReadableTrueTypeFontInfo(),
                OglRecipes.FONT_ATLAS, new ReadableFontAtlas(512, 512)
        );
        final Map<String, ReadableAsset> byExtension = Map.of(
                "vs", new ReadableShaderObject(GL20.GL_VERTEX_SHADER),
                "fs", new ReadableShaderObject(GL20.GL_FRAGMENT_SHADER),
                "png", readableTexture2d,
                "jpg", readableTexture2d,
                "conf", readableConfig,
                "ogg", new ReadableVorbisAudio()
        );
        return new GameAssets(
                new ManagedAssets(
                        new SimpleAssets(
                                resources,
                                new CompositeReadableAssets(
                                        new ResourceByKey(byKey),
                                        new ResourceByExtension(byExtension)
                                )
                        )
                )
        );
    }
}
