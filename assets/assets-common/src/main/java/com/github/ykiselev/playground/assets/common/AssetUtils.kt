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
package com.github.ykiselev.playground.assets.common

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.wrap.Wrap
import java.nio.channels.ReadableByteChannel

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 13.04.2019
 */
object AssetUtils {

    @JvmStatic
    fun <K, A, C> read(channel: ReadableByteChannel?, recipe: Recipe<K, A, C>?, assets: Assets): Wrap<A> =
        assets.resolve(recipe)
            .read(channel, recipe, assets)

    @JvmStatic
    fun <K, A, C> read(resource: String, recipe: Recipe<K, A, C>?, assets: Assets): Wrap<A> =
        assets.open(resource)
            ?.let {
                assets.resolve(resource, recipe).read(it, recipe, assets)
            } ?: throw ResourceException("Unable to read $resource")

}
