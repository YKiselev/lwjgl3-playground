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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * This implementation uses supplied instance of {@link ReadableAssets} to resolve {@link ReadableAsset}.
 * <p>
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class SimpleAssets implements Assets {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Resources resources;

    private final ReadableAssets readableAssets;

    public SimpleAssets(Resources resources, ReadableAssets readableAssets) {
        this.resources = requireNonNull(resources);
        this.readableAssets = requireNonNull(readableAssets);
    }

    @Override
    public <T> Optional<T> tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        return resources.open(resource)
                .map(channel ->
                        readableAssets.resolve(resource, clazz)
                                .read(channel, assets)
                ).map(v ->
                        logAsset(resource, v));
    }

    private <T> T logAsset(String resource, T object) {
        if (resource != null) {
            logger.debug("Loaded resource \"{}\" : \"{}\"", resource, object);
        }
        return object;
    }

    @Override
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return readableAssets.resolve(resource, clazz);
    }
}