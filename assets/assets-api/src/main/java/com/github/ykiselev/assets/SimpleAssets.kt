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
import org.slf4j.LoggerFactory
import java.nio.channels.ReadableByteChannel

/**
 * This implementation uses supplied instance of [ReadableAssets] to resolve [ReadableAsset].
 *
 *
 * Created by Y.Kiselev on 15.05.2016.
 */
class SimpleAssets(val resources: Resources, private val readableAssets: ReadableAssets) : Assets {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun <K, T, C> tryLoad(resource: String, recipe: Recipe<K, T, C>?, assets: Assets): Wrap<T>? =
        resources.open(resource)
            ?.let { channel: ReadableByteChannel? ->
                readableAssets.resolve(resource, recipe)
                    ?.read(channel, recipe, assets)
            }?.let {
                logger.debug("Loaded resource \"{}\" : \"{}\"", resource, it)
                it
            }

    override fun <K, T, C> resolve(resource: String?, recipe: Recipe<K, T, C>?): ReadableAsset<T, C>? =
        readableAssets.resolve(resource, recipe)

    override fun open(resource: String): ReadableByteChannel? =
        resources.open(resource)

    override fun openAll(resource: String): Sequence<ReadableByteChannel> =
        resources.openAll(resource)
}
