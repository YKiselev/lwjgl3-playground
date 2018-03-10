package com.github.ykiselev.assets.vorbis;

import com.github.ykiselev.common.Wrap;
import com.github.ykiselev.lifetime.Manageable;
import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.openal.Errors;
import org.lwjgl.openal.AL10;

import java.nio.ShortBuffer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.openal.AL10.alBufferData;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class VorbisAudio implements AudioSamples, Manageable<VorbisAudio> {

    private final Description description;

    private final Consumer<VorbisAudio> onClose;

    private VorbisAudio(Description description, Consumer<VorbisAudio> onClose) {
        this.description = requireNonNull(description);
        this.onClose = requireNonNull(onClose);
    }

    public VorbisAudio(int format, int sampleRate, int length, Wrap<ShortBuffer> wrap) {
        this(
                new Description(format, sampleRate, length, wrap),
                va -> va.description.delete()
        );
    }

    @Override
    public VorbisAudio manage(Consumer<VorbisAudio> onClose) {
        return new VorbisAudio(description, onClose);
    }

    @Override
    public void close() {
        onClose.accept(this);
    }

    @Override
    public int format() {
        return description.format;
    }

    @Override
    public int sampleRate() {
        return description.sampleRate;
    }

    @Override
    public int length() {
        return description.length;
    }

    @Override
    public void buffer(int buffer) {
        description.buffer(buffer);
    }

    private static final class Description {

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

        Description(int format, int sampleRate, int length, Wrap<ShortBuffer> wrap) {
            this.format = format;
            this.sampleRate = sampleRate;
            this.length = length;
            this.wrap = requireNonNull(wrap);
        }

        void delete() {
            wrap.close();
        }

        void buffer(int buffer) {
            alBufferData(buffer, format, wrap.value(), sampleRate);
            Errors.assertNoAlErrors();
        }

    }
}
