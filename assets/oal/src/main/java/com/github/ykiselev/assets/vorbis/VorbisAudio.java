package com.github.ykiselev.assets.vorbis;

import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.openal.Errors;
import com.github.ykiselev.wrap.Wrap;

import java.nio.ShortBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.openal.AL10.alBufferData;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class VorbisAudio implements AudioSamples {

    private final int format;

    private final int sampleRate;

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
}
