package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.CompositeReadableAssets;
import com.github.ykiselev.assets.ManagedAssets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ReadableVorbisAudio;
import com.github.ykiselev.assets.Resources;
import com.github.ykiselev.assets.SimpleAssets;
import com.github.ykiselev.assets.formats.ReadableConfig;
import com.github.ykiselev.assets.formats.ReadableObjModel;
import com.github.ykiselev.assets.formats.ReadableProgramObject;
import com.github.ykiselev.assets.formats.ReadableShaderObject;
import com.github.ykiselev.assets.formats.ReadableSpriteFont;
import com.github.ykiselev.assets.formats.ReadableTexture2d;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.ShaderObject;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameAssets {

    public static Assets create(Resources resources) {
        final ReadableConfig readableConfig = new ReadableConfig();
        final ReadableShaderObject readableShaderObject = new ReadableShaderObject();
        final ReadableTexture2d readableTexture2d = new ReadableTexture2d();
        final Map<Class, ReadableAsset> byClass = ImmutableMap.<Class, ReadableAsset>builder()
                .put(Config.class, readableConfig)
                .put(ProgramObject.class, new ReadableProgramObject())
                .put(ShaderObject.class, readableShaderObject)
                .put(SpriteFont.class, new ReadableSpriteFont())
                .put(Texture2d.class, readableTexture2d)
                .put(ObjModel.class, new ReadableObjModel())
                .build();
        final Map<String, ReadableAsset> byExtension = ImmutableMap.<String, ReadableAsset>builder()
                .put("vs", readableShaderObject)
                .put("fs", readableShaderObject)
                .put("png", readableTexture2d)
                .put("jpg", readableTexture2d)
                .put("conf", readableConfig)
                .put("ogg", new ReadableVorbisAudio())
                .build();
        return new ManagedAssets(
                new SimpleAssets(
                        resources,
                        new CompositeReadableAssets(
                                new ResourceByClass(byClass),
                                new ResourceByExtension(byExtension)
                        )
                ),
                new ConcurrentHashMap<>()
        );
    }
}
