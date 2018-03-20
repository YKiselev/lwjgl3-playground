package com.github.ykiselev.openal;

import org.lwjgl.openal.AL10;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface AudioSamples extends AutoCloseable {

    /**
     * @return the audio data format (like {@link AL10#AL_FORMAT_MONO8}, etc)
     */
    int format();

    /**
     * @return frequency in samples per second
     */
    int sampleRate();

    /**
     * @return total number of samples (per channel)
     */
    int length();

    /**
     * Buffers audio data in specified buffer
     *
     * @param buffer the OpenAL named buffer
     */
    void buffer(int buffer);
}
