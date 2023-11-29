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

import java.util.*

/**
 * Implementation of [ReadableAssets] which delegates resolution to configured delegates. Method resolve
 * iterates over delegates until first `non-null` [ReadableAsset] is returned.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CompositeReadableAssets(vararg delegates: ReadableAssets) : ReadableAssets {

    private val delegates = delegates.clone()

    init {
        require(delegates.isNotEmpty()) { "At least one delegate should be supplied!" }
    }

    override fun <K, T, C> resolve(resource: String?, recipe: Recipe<K, T, C>?): ReadableAsset<T, C>? {
        for (delegate in delegates) {
            val result = delegate.resolve(resource, recipe)
            if (result != null) {
                return result
            }
        }
        throw ResourceException("Unable to resolve resource \"$resource\" using recipe \"$recipe\"")
    }
}
