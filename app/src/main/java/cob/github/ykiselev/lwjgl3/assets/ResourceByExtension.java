package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.formats.ReadableConfig;
import com.github.ykiselev.assets.formats.ReadableShaderObject;
import com.github.ykiselev.assets.formats.ReadableTexture2d;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ResourceByExtension implements Function<String, ReadableResource> {

    private final Map<String, ReadableResource> map;

    public ResourceByExtension(Map<String, ReadableResource> map) {
        this.map = map;
    }

    public ResourceByExtension() {
        this(
                ImmutableMap.<String, ReadableResource>builder()
                        .put("vs", new ReadableShaderObject())
                        .put("fs", new ReadableShaderObject())
                        .put("png", new ReadableTexture2d())
                        .put("jpg", new ReadableTexture2d())
                        .put("conf", new ReadableConfig())
                        .build()
        );
    }

    @Override
    public ReadableResource apply(String resource) {
        return map.get(resource);
    }
}
