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
import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.openal.assets.ReadableVorbisAudio;
import com.github.ykiselev.wrap.Wrap;
import org.junit.jupiter.api.Test;

import java.nio.channels.Channels;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ReadableVorbisAudioTest {

    private final ReadableAsset<AudioSamples> readableResource = new ReadableVorbisAudio(32 * 1024);

    private final Assets assets = mock(Assets.class);

    @Test
    public void shouldRead() {
        final Wrap<AudioSamples> samples = readableResource.read(
                Channels.newChannel(getClass().getResourceAsStream("/sample.ogg")),
                assets
        );
        assertNotNull(samples);
    }
}