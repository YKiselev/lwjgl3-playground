package com.github.ykiselev.assets;

import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.wrap.Wrap;
import org.junit.jupiter.api.Test;

import java.nio.channels.Channels;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ReadableVorbisAudioTest {

    private final ReadableAsset<AudioSamples> readableResource = new ReadableVorbisAudio(32 * 1024);

    private final Assets assets = mock(Assets.class);

    @Test
    void shouldRead() {
        final Wrap<AudioSamples> samples = readableResource.read(
                Channels.newChannel(getClass().getResourceAsStream("/sample.ogg")),
                assets
        );
        assertNotNull(samples);
    }
}