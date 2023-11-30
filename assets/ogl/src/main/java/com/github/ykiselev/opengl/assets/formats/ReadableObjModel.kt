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
import com.github.ykiselev.opengl.assets.formats.obj.ObjModel
import com.github.ykiselev.opengl.assets.formats.obj.ObjModelBuilder
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.noop
import java.io.BufferedReader
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ReadableObjModel : ReadableAsset<ObjModel, Void> {

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, ObjModel, Void>?,
        assets: Assets
    ): Wrap<ObjModel> =
        try {
            reader(channel).use { reader ->
                noop(
                    parse(reader)
                )
            }
        } catch (e: Exception) {
            throw ResourceException(e)
        }

    private fun reader(channel: ReadableByteChannel): BufferedReader =
        BufferedReader(
            Channels.newReader(
                channel,
                StandardCharsets.UTF_8.newDecoder(), -1
            )
        )

    private fun parse(reader: BufferedReader): ObjModel =
        with(ObjModelBuilder()) {
            generateSequence {
                reader.readLine()
            }.forEach {
                parseLine(it)
            }
            build()
        }
}
