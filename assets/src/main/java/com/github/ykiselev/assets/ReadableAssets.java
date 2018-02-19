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

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ReadableAssets {

    /**
     * Resolves instance of {@link ReadableAsset} from supplied URI and/or class. Some implementations (like {@link SimpleAssets}) require only one
     * of {code resource}, {@code clazz} to be {@code non-null}.
     *
     * @param <T>      the type of resource
     * @param resource the resource name.
     * @param clazz    the resource class.
     * @return the readable resource or {@code null} if not found.
     * @throws ResourceException if something goes wrong
     */
    <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException;

    /**
     * Convenient method to resolve {@link ReadableAsset} by asset class.
     *
     * @param clazz the asset class.
     * @param <T>   the type of asset class.
     * @return the readable resource or {@code null} if not found.
     * @throws ResourceException if something goes wrong
     */
    default <T> ReadableAsset<T> resolve(Class<T> clazz) throws ResourceException {
        return resolve(null, clazz);
    }

    /**
     * Convenient method to resolve {@link ReadableAsset} by asset class.
     *
     * @param resource the resource name.
     * @param <T>      the type of asset class.
     * @return the readable resource or {@code null} if not found.
     * @throws ResourceException if something goes wrong
     */
    default <T> ReadableAsset<T> resolve(String resource) throws ResourceException {
        return resolve(resource, null);
    }
}
