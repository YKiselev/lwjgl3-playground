package com.github.ykiselev.assets;

import com.github.ykiselev.assets.vorbis.VorbisAudio;
import com.github.ykiselev.common.Wrap;
import com.github.ykiselev.io.ByteChannelAsMemoryUtilByteBuffer;
import com.github.ykiselev.io.ReadableBytes;
import com.github.ykiselev.memory.MemAllocShort;
import com.github.ykiselev.openal.AudioSamples;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_info;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_samples_short_interleaved;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_open_memory;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_stream_length_in_samples;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ReadableVorbisAudio implements ReadableResource<AudioSamples> {

    private final int bufferSize;

    public ReadableVorbisAudio(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public AudioSamples read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException {
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            final ReadableBytes asBuffer = new ByteChannelAsMemoryUtilByteBuffer(
                    channel, bufferSize
            );
            try (Wrap<ByteBuffer> vorbis = asBuffer.read()) {
                try (MemoryStack ms = MemoryStack.stackPush()) {
                    final IntBuffer error = ms.ints(0);
                    final long decoder = stb_vorbis_open_memory(vorbis.value(), error, null);
                    if (decoder == NULL) {
                        throw new ResourceException("Failed to open " + resource + ": Error code " + error.get(0));
                    }
                    stb_vorbis_get_info(decoder, info);
                    final int channels = info.channels();
                    if (channels < 1 || channels > 2) {
                        throw new ResourceException("Failed to open " + resource + ": Unsupported number of channels - " + channels);
                    }
                    final MemAllocShort wrap = new MemAllocShort(
                            stb_vorbis_stream_length_in_samples(decoder)
                    );
                    final ShortBuffer pcm = wrap.value();
                    final int samples = stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
                    pcm.limit(samples * channels);
                    stb_vorbis_close(decoder);
                    return new VorbisAudio(
                            channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
                            info.sample_rate(),
                            samples,
                            wrap
                    );
                }
            }
        }
    }
}
