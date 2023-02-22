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

package com.github.ykiselev.openal.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.Recipe;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.common.io.ByteChannelAsByteBuffer;
import com.github.ykiselev.common.io.ReadableBytes;
import com.github.ykiselev.common.memory.MemAllocShort;
import com.github.ykiselev.common.pools.ByteChannelAsByteBufferPool;
import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.openal.assets.vorbis.VorbisAudio;
import com.github.ykiselev.wrap.Wrap;
import com.github.ykiselev.wrap.Wraps;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableVorbisAudio implements ReadableAsset<AudioSamples, Void> {

    private final int bufferSize;

    public ReadableVorbisAudio(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public ReadableVorbisAudio() {
        this(256 * 1024);
    }

    @Override
    public Wrap<AudioSamples> read(ReadableByteChannel channel, Recipe<?, AudioSamples, Void> recipe, Assets assets) throws ResourceException {
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            try (Wrap<ByteBuffer> vorbis = ByteChannelAsByteBufferPool.read(channel)) {
                try (MemoryStack ms = MemoryStack.stackPush()) {
                    final IntBuffer error = ms.ints(0);
                    final long decoder = stb_vorbis_open_memory(vorbis.value(), error, null);
                    if (decoder == NULL) {
                        throw new ResourceException("Failed to open resource: Error code " + error.get(0));
                    }
                    stb_vorbis_get_info(decoder, info);
                    final int channels = info.channels();
                    if (channels < 1 || channels > 2) {
                        throw new ResourceException("Failed to open resource: Unsupported number of channels - " + channels);
                    }
                    final MemAllocShort wrap = new MemAllocShort(
                            stb_vorbis_stream_length_in_samples(decoder)
                    );
                    final ShortBuffer pcm = wrap.value();
                    final int samples = stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
                    pcm.limit(samples * channels);
                    stb_vorbis_close(decoder);
                    return Wraps.of(
                            new VorbisAudio(
                                    channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
                                    info.sample_rate(),
                                    samples,
                                    wrap
                            )
                    );
                }
            }
        }
    }
}
