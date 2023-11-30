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
package com.github.ykiselev.openal.assets

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.DefaultRecipe.Companion.of
import com.github.ykiselev.assets.DefaultRecipe.Dummy
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.openal.AudioSamples
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.nio.channels.Channels

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ReadableVorbisAudioTest {
    private val readableResource: ReadableAsset<AudioSamples, Dummy> = ReadableVorbisAudio()
    private val assets = Mockito.mock(Assets::class.java)

    @Test
    fun shouldRead() {
        val samples = readableResource.read(
            Channels.newChannel(javaClass.getResourceAsStream("/sample.ogg")),
            of(AudioSamples::class.java),
            assets
        )
        Assertions.assertNotNull(samples)
    }
}