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
package com.github.ykiselev.opengl.assets.formats

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.common.pools.ByteChannelAsByteBufferPool
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.fonts.TrueTypeFontInfo
import com.github.ykiselev.playground.assets.common.AssetUtils.read
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 08.04.2019
 */
class ReadableTrueTypeFontInfo(private val scale: Float) : ReadableAsset<TrueTypeFontInfo, Void> {

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, TrueTypeFontInfo, Void>?,
        assets: Assets
    ): Wrap<TrueTypeFontInfo> =
        assets.load("fonts/default/font.conf", OglRecipes.CONFIG).use { fallback ->
            read(channel, OglRecipes.CONFIG, assets).use { config ->
                val fontConfig = config.value()
                    .withFallback(fallback.value())
                val ttf: Wrap<ByteBuffer> = assets.open(fontConfig.getString("asset"))
                    ?.let { ByteChannelAsByteBufferPool.read(it) }
                    ?: throw ResourceException("Unable to load font: \"" + fontConfig.getString("asset") + "\"")
                return of(
                    TrueTypeFontInfo.load(
                        ttf,
                        (fontConfig.getDouble("size") * scale).toFloat()
                    )
                )
            }
        }
}
