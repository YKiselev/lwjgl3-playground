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

import com.typesafe.config.ConfigFactory
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 10.04.2019
 */
class ReadableFontAtlasBuilderTest {

    @Test
    fun shouldReadCodePoints() {
        assertArrayEquals(
            intArrayOf(' '.code, 'a'.code, 'b'.code, 'c'.code, 'd'.code, 'e'.code, 'f'.code, 'Z'.code, 43981, 66561),
            ReadableFontAtlas.readCodePoints(listOf("\u0020", "a-f", "Z", "\uabcd", "\ud801\udc01")).toArray()
        )
    }

    @Test
    @Disabled
    fun shouldLoad() {
        val cfg = ConfigFactory.load("font-atlas.conf")
        println(cfg.getConfig("fonts").root().entries)
        for (s in cfg.getStringList("code-points")) {
            println(s)
        }
    }
}
