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
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;
import com.github.ykiselev.assets.SimpleAssets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.openal.assets.ReadableVorbisAudio;
import com.github.ykiselev.opengl.assets.formats.ReadableConfig;
import com.github.ykiselev.opengl.assets.formats.ReadableObjModel;
import com.github.ykiselev.opengl.assets.formats.ReadableProgramObject;
import com.github.ykiselev.opengl.assets.formats.ReadableShaderObject;
import com.github.ykiselev.opengl.assets.formats.ReadableSpriteFont;
import com.github.ykiselev.opengl.assets.formats.ReadableTexture2d;
import com.github.ykiselev.opengl.assets.formats.ReadableTrueTypeFontInfo;
import com.github.ykiselev.opengl.assets.formats.obj.ObjModel;
import com.github.ykiselev.opengl.fonts.TrueTypeFontInfo;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.DefaultMipMappedTexture2d;
import com.github.ykiselev.opengl.textures.DefaultSprite;
import com.github.ykiselev.opengl.textures.MipMappedTexture2d;
import com.github.ykiselev.opengl.textures.Sprite;
import com.github.ykiselev.wrap.Wrap;
import com.typesafe.config.Config;
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
    public <T> Wrap<T> tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        return delegate.tryLoad(resource, clazz, assets);
    }

    @Override
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return delegate.resolve(resource, clazz);
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
        final ReadableTexture2d simpleReadableTexture2d = new ReadableTexture2d(
                DefaultSprite::new, false
        );
        final ReadableTexture2d mipMappedReadableTexture2d = new ReadableTexture2d(
                DefaultMipMappedTexture2d::new, true
        );
        final Map<Class, ReadableAsset> byClass = Map.of(
                Config.class, readableConfig,
                ProgramObject.class, new ReadableProgramObject(),
                SpriteFont.class, new ReadableSpriteFont(),
                Sprite.class, simpleReadableTexture2d,
                MipMappedTexture2d.class, mipMappedReadableTexture2d,
                ObjModel.class, new ReadableObjModel(),
                TrueTypeFontInfo.class, new ReadableTrueTypeFontInfo()
        );
        final Map<String, ReadableAsset> byExtension = Map.of(
                "vs", new ReadableShaderObject(GL20.GL_VERTEX_SHADER),
                "fs", new ReadableShaderObject(GL20.GL_FRAGMENT_SHADER),
                "png", simpleReadableTexture2d,
                "jpg", simpleReadableTexture2d,
                "conf", readableConfig,
                "ogg", new ReadableVorbisAudio()
        );
        return new GameAssets(
                new ManagedAssets(
                        new SimpleAssets(
                                resources,
                                new CompositeReadableAssets(
                                        new ResourceByClass(byClass),
                                        new ResourceByExtension(byExtension)
                                )
                        )
                )
        );
    }
}
