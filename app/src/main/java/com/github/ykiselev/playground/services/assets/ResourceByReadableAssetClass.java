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
import com.github.ykiselev.assets.ResourceException;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Special version of {@link ReadableAssets} which checks if supplied class implements {@link ReadableAsset} and if yes instantiates it and returns.
 *
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 08.04.2019
 */
public final class ResourceByReadableAssetClass implements ReadableAssets {

    private final Function<Class<? extends ReadableAsset<?>>, ReadableAsset<?>> mapper;

    /**
     * Primary ctor.
     *
     * @param mapper the mapper function to get instance of supplied class object.
     */
    public ResourceByReadableAssetClass(Function<Class<? extends ReadableAsset<?>>, ReadableAsset<?>> mapper) {
        this.mapper = requireNonNull(mapper);
    }

    /**
     * Default version which instantiates supplied class every time without any caching
     */
    public ResourceByReadableAssetClass() {
        this(clazz -> create((Class<? extends ReadableAsset<?>>) clazz));
    }

    /**
     * If supplied {@code clazz} implements {@link ReadableAsset} then method tries to instantiate it and return.
     *
     * @param resource the resource name.
     * @param clazz    the class of resource or readable resource.
     * @return the readable resource or {@code null}
     * @throws ResourceException if something goes wrong
     */
    @Override
    @SuppressWarnings("unchecked")
    public ReadableAsset resolve(String resource, Class<?> clazz) throws ResourceException {
        if (ReadableAsset.class.isAssignableFrom(clazz)) {
            return mapper.apply((Class<? extends ReadableAsset<?>>) clazz);
        }
        return null;
    }

    public static <R extends ReadableAsset<?>> ReadableAsset<?> create(Class<R> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

}
