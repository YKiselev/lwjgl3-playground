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
import com.github.ykiselev.common.memory.MemAlloc
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.fonts.*
import com.github.ykiselev.playground.assets.common.AssetUtils.read
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.stream.IntStream

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 09.04.2019
 */
class ReadableFontAtlas(private val bitmapFactory: () -> Bitmap<Wrap<ByteBuffer>>) : ReadableAsset<FontAtlas, Void> {

    /**
     * @param width  the width to use for bitmaps
     * @param height the height to use for bitmaps
     * @see [FontAtlasBuilder]
     */
    constructor(width: Int, height: Int) : this({
        Bitmap<Wrap<ByteBuffer>>(
            width,
            height,
            MemAlloc(width * height)
        )
    })

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, FontAtlas, Void>?,
        assets: Assets
    ): Wrap<FontAtlas> =
        read(channel, OglRecipes.CONFIG, assets).use { cfg ->
            val atlasConfig = cfg.value()
            val fonts = atlasConfig.getConfig("fonts").root()
                .entries
                .asSequence()
                .mapNotNull { (key, value1): Map.Entry<String, ConfigValue> ->
                    val value = value1.unwrapped() as String
                    if (value.isEmpty()) {
                        null
                    } else key to assets.load(value, OglRecipes.TRUE_TYPE_FONT_INFO)
                    // Sort array from smallest font to larges to improve glyph texture fill rate
                }.sortedWith(Comparator.comparingDouble { (_, value) ->
                    value.value().metrics().fontSize()
                        .toDouble()
                }).toList()
            val codePoints = CodePoints.of(readCodePoints(atlasConfig.getStringList("code-points")))
            FontAtlasBuilder(bitmapFactory).use { atlas ->
                fonts.forEach { (key, value) ->
                    atlas.addFont(
                        key, value, codePoints
                    )
                }
                of(
                    DefaultFontAtlas(
                        atlas.drainFonts()
                    )
                )
            }
        }

    companion object {
        fun readCodePoints(src: Collection<String>): IntStream =
            src.stream().flatMapToInt { s: String ->
                val idx = s.indexOf("-")
                if (idx != -1) {
                    IntStream.range(
                        cp(s, 0, idx),
                        cp(s, idx + 1, s.length) + 1
                    )
                } else {
                    IntStream.of(cp(s, 0, s.length))
                }
            }

        /**
         * Extracts single code point from the given string starting at `from` index and ending right before `to` index.
         *
         * @param src  the source string
         * @param from from index (inclusive)
         * @param to   to index (exclusive)
         * @return the code point
         */
        private fun cp(src: String, from: Int, to: Int): Int {
            val result = src.codePointAt(from)
            val count = Character.charCount(result)
            require(from + count == to) { "Bad code point: \"" + src.substring(from, to) + "\"" }
            return result
        }
    }
}
