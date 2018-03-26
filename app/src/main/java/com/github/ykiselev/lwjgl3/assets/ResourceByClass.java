package com.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ReadableAssets;
import com.github.ykiselev.assets.ResourceException;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ResourceByClass implements ReadableAssets {

    private final Map<Class, ReadableAsset> map;

    public ResourceByClass(Map<Class, ReadableAsset> map) {
        this.map = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return map.get(clazz);
    }
}
