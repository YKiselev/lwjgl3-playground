package com.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ReadableAssets;
import com.github.ykiselev.assets.ResourceException;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ResourceByExtension implements ReadableAssets {

    private final Map<String, ReadableAsset> map;

    public ResourceByExtension(Map<String, ReadableAsset> map) {
        this.map = map;
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
