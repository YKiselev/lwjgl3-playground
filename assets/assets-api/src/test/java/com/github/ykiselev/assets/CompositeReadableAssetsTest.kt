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

import com.github.ykiselev.assets.DefaultRecipe.Companion.of
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.nio.channels.ReadableByteChannel

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CompositeReadableAssetsTest {

    @Test
    fun shouldResolve() {
        val rr: ReadableAsset<String, Void> = object : ReadableAsset<String, Void> {
            override fun read(
                channel: ReadableByteChannel,
                recipe: Recipe<*, String, Void>?,
                assets: Assets
            ): Wrap<String> = Wraps.noop("test")
        }
        val delegate1 = mock<ReadableAssets>()
        val delegate2 = mock<ReadableAssets>()
        `when`(
            delegate1.resolve(
                eq("a"), any<Recipe<Any, String, Void>>()
            )
        ).thenReturn(null)
        `when`(
            delegate2.resolve(
                eq("a"), any<Recipe<Any, String, Void>>()
            )
        ).thenReturn(rr)
        val readableAssets: ReadableAssets = CompositeReadableAssets(
            delegate1,
            delegate2
        )
        assertEquals(
            rr,
            readableAssets.resolve(
                "a", of(String::class.java)
            )
        )
    }
}