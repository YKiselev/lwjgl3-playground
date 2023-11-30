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
import com.github.ykiselev.assets.DefaultRecipe.Dummy
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.common.memory.MemAllocShort
import com.github.ykiselev.common.pools.ByteChannelAsByteBufferPool
import com.github.ykiselev.openal.AudioSamples
import com.github.ykiselev.openal.assets.vorbis.VorbisAudio
import com.github.ykiselev.wrap.Wrap
import com.github.ykiselev.wrap.Wraps.of
import org.lwjgl.openal.AL10
import org.lwjgl.stb.STBVorbis
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.channels.ReadableByteChannel

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ReadableVorbisAudio : ReadableAsset<AudioSamples, Dummy> {

    override fun read(
        channel: ReadableByteChannel,
        recipe: Recipe<*, AudioSamples, Dummy>?,
        assets: Assets
    ): Wrap<AudioSamples> =
        STBVorbisInfo.malloc().use { info ->
            ByteChannelAsByteBufferPool.read(channel).use { vorbis ->
                MemoryStack.stackPush().use { ms ->
                    val error = ms.ints(0)
                    val decoder = STBVorbis.stb_vorbis_open_memory(vorbis.value(), error, null)
                    if (decoder == MemoryUtil.NULL) {
                        throw ResourceException("Failed to open resource: Error code " + error[0])
                    }
                    STBVorbis.stb_vorbis_get_info(decoder, info)
                    val channels = info.channels()
                    if (channels < 1 || channels > 2) {
                        throw ResourceException("Failed to open resource: Unsupported number of channels - $channels")
                    }
                    val wrap = MemAllocShort(
                        STBVorbis.stb_vorbis_stream_length_in_samples(decoder)
                    )
                    val pcm = wrap.value()
                    val samples = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm)
                    pcm.limit(samples * channels)
                    STBVorbis.stb_vorbis_close(decoder)
                    of(
                        VorbisAudio(
                            if (channels == 1) AL10.AL_FORMAT_MONO16 else AL10.AL_FORMAT_STEREO16,
                            info.sample_rate(),
                            samples,
                            wrap
                        )
                    )
                }
            }
        }
}
