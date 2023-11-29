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

import com.github.ykiselev.wrap.Wraps.noop
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.nio.channels.ReadableByteChannel

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class SimpleAssetsTest {

    private val resources = mock<Resources>()
    private val readableAssets: ReadableAssets = mock<ReadableAssets>()
    private val readableAsset = mock<ReadableAsset<Double, Void>>()
    private val assets: Assets = SimpleAssets(resources, readableAssets)

    @BeforeEach
    fun setUp() {
        `when`(resources.open(any<String>()))
            .thenReturn(
                mock<ReadableByteChannel>()
            )
        `when`(readableAsset.read(any<ReadableByteChannel>(), any<Recipe<*, Double, Void>>(), any()))
            .thenReturn(noop(Math.PI))
    }

    @Test
    fun shouldLoad() {
        `when`(readableAssets.resolve(any<String>(), any<Recipe<*, Double, Void>>()))
            .thenReturn(readableAsset)
        `when`(readableAsset.read(any<ReadableByteChannel>(), any<Recipe<*, Double, Void>>(), eq(assets)))
            .thenReturn(noop(Math.PI))
        Assertions.assertEquals(
            Math.PI, assets.load(
                "x", DefaultRecipe.of(
                    Double::class.java
                )
            ).value(), 0.00001
        )
    }
}