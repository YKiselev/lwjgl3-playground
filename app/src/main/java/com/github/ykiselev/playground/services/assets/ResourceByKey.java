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
public final class ResourceByKey<Key> implements ReadableAssets {

    private final Map<Key, ReadableAsset<?, ?>> map;

    public ResourceByKey(Map<Key, ReadableAsset<?, ?>> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    public <K, T, C> ReadableAsset<T, C> resolve(String resource, Recipe<K, T, C> recipe) throws ResourceException {
        if (recipe == null) {
            return null;
        }
        return (ReadableAsset<T, C>) map.get((Key) recipe.key());
    }
}
