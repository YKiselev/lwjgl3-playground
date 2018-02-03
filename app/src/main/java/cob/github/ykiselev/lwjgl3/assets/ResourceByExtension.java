package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ReadableAssets;
import com.github.ykiselev.assets.ReadableVorbisAudio;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.formats.ReadableConfig;
import com.github.ykiselev.assets.formats.ReadableShaderObject;
import com.github.ykiselev.assets.formats.ReadableTexture2d;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ResourceByExtension implements ReadableAssets {

    private final Map<String, ReadableAsset> map;

    public ResourceByExtension(Map<String, ReadableAsset> map) {
        this.map = map;
    }

    public ResourceByExtension() {
        this(
                ImmutableMap.<String, ReadableAsset>builder()
                        .put("vs", new ReadableShaderObject())
                        .put("fs", new ReadableShaderObject())
                        .put("png", new ReadableTexture2d())
                        .put("jpg", new ReadableTexture2d())
                        .put("conf", new ReadableConfig())
                        .put("ogg", new ReadableVorbisAudio(128 * 1024))
                        .build()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return map.get(extension(resource));
    }

    private String extension(String resource) {
        if (resource == null) {
            return null;
        }
        final int i = resource.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        return resource.substring(i + 1);
    }

}
