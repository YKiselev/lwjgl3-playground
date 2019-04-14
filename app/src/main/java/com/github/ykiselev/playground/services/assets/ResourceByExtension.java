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

import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ReadableAssets;
import com.github.ykiselev.assets.Recipe;
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
    public <K, T, C> ReadableAsset<T, C> resolve(String resource, Recipe<K, T, C> recipe) throws ResourceException {
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
