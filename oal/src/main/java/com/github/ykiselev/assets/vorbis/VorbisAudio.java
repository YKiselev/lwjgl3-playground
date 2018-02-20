package com.github.ykiselev.assets.vorbis;

import com.github.ykiselev.caching.LongWeighted;
import com.github.ykiselev.common.Wrap;
import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.openal.Errors;
import org.lwjgl.openal.AL10;

import java.nio.ShortBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.openal.AL10.alBufferData;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class VorbisAudio implements AudioSamples, LongWeighted {

    /**
     * One of AL formats, for example - {@link AL10#AL_FORMAT_MONO16} or {@link AL10#AL_FORMAT_STEREO16}
     */
    private final int format;

    private final int sampleRate;

    /**
     * Length in samples
     */
    private final int length;

    private final Wrap<ShortBuffer> wrap;

    public VorbisAudio(int format, int sampleRate, int length, Wrap<ShortBuffer> wrap) {
        this.format = format;
        this.sampleRate = sampleRate;
        this.length = length;
        this.wrap = requireNonNull(wrap);
    }

    @Override
    public void close() {
        wrap.close();
    }

    @Override
    public int format() {
        return format;
    }

    @Override
    public int sampleRate() {
        return sampleRate;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void buffer(int buffer) {
        alBufferData(buffer, format, wrap.value(), sampleRate);
        Errors.assertNoAlErrors();
    }

    @Override
    public long weight() {
        return wrap.value().limit() * Short.BYTES;
    }
}
