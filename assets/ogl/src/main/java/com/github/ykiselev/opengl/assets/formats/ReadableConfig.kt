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
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.noop
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.BufferedReader
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets

/**
 * Created by Y.Kiselev on 14.06.2016.
 */
class ReadableConfig : ReadableAsset<Config, Void> {

    override fun read(channel: ReadableByteChannel, recipe: Recipe<*, Config, Void>?, assets: Assets): Wrap<Config> =
        BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8.newDecoder(), -1)).use { reader ->
            return noop(
                ConfigFactory.parseReader(reader)
            )
        }
}
