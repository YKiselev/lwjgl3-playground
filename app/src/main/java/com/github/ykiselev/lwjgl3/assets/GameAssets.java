package com.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.CompositeReadableAssets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ReadableVorbisAudio;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;
import com.github.ykiselev.assets.SimpleAssets;
import com.github.ykiselev.assets.formats.ReadableConfig;
import com.github.ykiselev.assets.formats.ReadableObjModel;
import com.github.ykiselev.assets.formats.ReadableProgramObject;
import com.github.ykiselev.assets.formats.ReadableShaderObject;
import com.github.ykiselev.assets.formats.ReadableSpriteFont;
import com.github.ykiselev.assets.formats.ReadableTexture2d;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.DefaultMipMappedTexture2d;
import com.github.ykiselev.opengl.textures.DefaultSimpleTexture2d;
import com.github.ykiselev.opengl.textures.MipMappedTexture2d;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import org.lwjgl.opengl.GL20;

import java.util.Map;

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
    public <T> T tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
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

    public static Assets create(Resources resources) {
        final ReadableConfig readableConfig = new ReadableConfig();
        final ReadableTexture2d simpleReadableTexture2d = new ReadableTexture2d(
                DefaultSimpleTexture2d::new, false
        );
        final ReadableTexture2d mipMappedReadableTexture2d = new ReadableTexture2d(
                DefaultMipMappedTexture2d::new, true
        );
        final Map<Class, ReadableAsset> byClass = ImmutableMap.<Class, ReadableAsset>builder()
                .put(Config.class, readableConfig)
                .put(ProgramObject.class, new ReadableProgramObject())
                .put(SpriteFont.class, new ReadableSpriteFont())
                .put(SimpleTexture2d.class, simpleReadableTexture2d)
                .put(MipMappedTexture2d.class, mipMappedReadableTexture2d)
                .put(ObjModel.class, new ReadableObjModel())
                .build();
        final Map<String, ReadableAsset> byExtension = ImmutableMap.<String, ReadableAsset>builder()
                .put("vs", new ReadableShaderObject(GL20.GL_VERTEX_SHADER))
                .put("fs", new ReadableShaderObject(GL20.GL_FRAGMENT_SHADER))
                .put("png", simpleReadableTexture2d)
                .put("jpg", simpleReadableTexture2d)
                .put("conf", readableConfig)
                .put("ogg", new ReadableVorbisAudio())
                .build();
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
