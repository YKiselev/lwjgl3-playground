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

package com.github.ykiselev.assets;

import java.util.Objects;

/**
 * Implementation of {@link ReadableAssets} which delegates resolution to configured delegates. Method resolve
 * iterates over delegates until first {@code non-null} {@link ReadableAsset} is returned.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CompositeReadableAssets implements ReadableAssets {

    private final ReadableAssets[] delegates;

    public CompositeReadableAssets(ReadableAssets... delegates) {
        Objects.requireNonNull(delegates);
        if (delegates.length == 0) {
            throw new IllegalArgumentException("At least one delegate should be supplied!");
        }
        this.delegates = delegates.clone();
    }

    @Override
    public <K, T, C> ReadableAsset<T, C> resolve(String resource, Recipe<K, T, C> recipe) throws ResourceException {
        for (ReadableAssets delegate : delegates) {
            final ReadableAsset<T, C> result = delegate.resolve(resource, recipe);
            if (result != null) {
                return result;
            }
        }
        throw new ResourceException("Unable to resolve resource \"" + resource + "\" using recipe \"" + recipe + "\"");
    }
}
