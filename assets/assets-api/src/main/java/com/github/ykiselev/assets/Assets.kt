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
package com.github.ykiselev.assets

import com.github.ykiselev.wrap.Wrap

/**
 * Asset manager. Implementations expected to delegate actual work of loading asset to appropriate instance of class implementing [ReadableAsset].
 *
 *
 * Created by Y.Kiselev on 15.05.2016.
 */
interface Assets : ReadableAssets, Resources {
    /**
     * Loads asset using one of registered [ReadableAsset]'s
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @param recipe   the recipe to use for cooking of resource or `null` if not required
     * @return the requested resource
     * @throws ResourceException if resource not found or something goes wrong during the resource loading process.
    </T> */
    @Throws(ResourceException::class)
    fun <K, T, C> load(resource: String, recipe: Recipe<K, T, C>?): Wrap<T> {
        return tryLoad(resource, recipe) ?: throw ResourceException("Unable to load $resource")
    }

    /**
     * Loads asset using one of registered [ReadableAsset]'s
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @param recipe   the recipe to use for cooking of resource or `null` if not required
     * @return the requested resource or `null`
     * @throws ResourceException if something goes wrong during the resource loading process.
    </T> */
    @Throws(ResourceException::class)
    fun <K, T, C> tryLoad(resource: String, recipe: Recipe<K, T, C>?): Wrap<T>? {
        return tryLoad(resource, recipe, this)
    }

    /**
     * Loads asset using one of registered [ReadableAsset]'s
     *
     * @param <T>      the type of resource
     * @param resource the resource name
     * @param recipe   the recipe to use for cooking of resource or `null` if not required
     * @param assets   the asset manager to pass to [ReadableAsset.read] to load sub-assets
     * @return the requested resource or `null`
     * @throws ResourceException if something goes wrong during the resource loading process.
    </T> */
    @Throws(ResourceException::class)
    fun <K, T, C> tryLoad(resource: String, recipe: Recipe<K, T, C>?, assets: Assets): Wrap<T>?
}
