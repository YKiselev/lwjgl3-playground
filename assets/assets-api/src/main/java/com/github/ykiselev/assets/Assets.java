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

import com.github.ykiselev.wrap.Wrap;

/**
 * Asset manager. Implementations expected to delegate actual work of loading asset to appropriate instance of class implementing {@link ReadableAsset}.
 * <p>
 * Created by Y.Kiselev on 15.05.2016.
 */
public interface Assets extends ReadableAssets, Resources {

    /**
     * Loads asset using one of registered {@link ReadableAsset}'s
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @param clazz    the class of resource or {@code null} if not known
     * @return the requested resource
     * @throws ResourceException if resource not found or something goes wrong during the resource loading process.
     */
    default <T> Wrap<T> load(String resource, Class<?> clazz) throws ResourceException {
        final Wrap<T> result = tryLoad(resource, clazz);
        if (result == null) {
            throw new ResourceException("Unable to load " + resource);
        }
        return result;
    }

    /**
     * Loads asset using one of registered {@link ReadableAsset}'s
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @param clazz    the class of resource or {@code null} if not known
     * @return the requested resource or {@code null}
     * @throws ResourceException if something goes wrong during the resource loading process.
     */
    default <T> Wrap<T> tryLoad(String resource, Class<?> clazz) throws ResourceException {
        return tryLoad(resource, clazz, this);
    }

    /**
     * Loads asset using one of registered {@link ReadableAsset}'s
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @param clazz    the class of resource or {@code null} if not known
     * @param assets   the asset manager to pass to {@link ReadableAsset#read(java.nio.channels.ReadableByteChannel, Assets)} to load sub-assets
     * @return the requested resource or {@code null}
     * @throws ResourceException if something goes wrong during the resource loading process.
     */
    <T> Wrap<T> tryLoad(String resource, Class<?> clazz, Assets assets) throws ResourceException;

    /**
     * Convenient method taking only one string argument as a resource name.
     *
     * @param <T>      the type of resource
     * @param resource the resource name.
     * @return the requested resource
     * @throws ResourceException if something goes wrong during the resource loading process.
     */
    default <T> Wrap<T> load(String resource) throws ResourceException {
        return load(resource, null);
    }

    /**
     * Convenient method taking only one string argument as a resource name.
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @return the requested resource or {@code null}
     * @throws ResourceException if something goes wrong during the resource loading process.
     */
    default <T> Wrap<T> tryLoad(String resource) throws ResourceException {
        return tryLoad(resource, null);
    }
}
