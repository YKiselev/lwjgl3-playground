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
import com.github.ykiselev.assets.DefaultRecipe.Companion.of
import com.github.ykiselev.opengl.assets.formats.obj.ObjModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.nio.channels.Channels

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ReadableObjModelTest {

    private val resource = ReadableObjModel()
    private val assets = mock<Assets>()
    private val url = javaClass.getResource("/models/2cubes.obj")

    @Test
    fun shouldRead() {
        val model = resource.read(
            Channels.newChannel(
                url.openStream()
            ),
            of(ObjModel::class.java),
            assets
        )
        Assertions.assertNotNull(model)
    }
}