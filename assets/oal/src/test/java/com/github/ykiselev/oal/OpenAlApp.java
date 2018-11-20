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

package com.github.ykiselev.oal;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.openal.assets.ReadableVorbisAudio;
import com.github.ykiselev.openal.AudioSamples;
import com.github.ykiselev.wrap.Wrap;
import org.junit.jupiter.api.Assertions;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;

import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.util.List;

import static com.github.ykiselev.openal.Errors.assertNoAlErrors;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_BUFFERS_QUEUED;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.ALC_FREQUENCY;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.ALC10.ALC_TRUE;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.mockito.Mockito.mock;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class OpenAlApp {

    private final ReadableAsset<AudioSamples> readableResource = new ReadableVorbisAudio(32 * 1024);

    private final Assets assets = mock(Assets.class);

    public static void main(String[] args) throws Exception {
        new OpenAlApp().run();
    }

    private void run() throws Exception {
        final long device = alcOpenDevice((CharSequence) null);
        if (device == NULL) {
            throw new IllegalArgumentException("No device found!");
        }

        try {
            final ALCCapabilities capabilities = createCapabilities(device);
            if (!capabilities.OpenALC10) {
                throw new IllegalArgumentException("OpenAL 1.0 is not supported!");
            }
            System.out.println("OpenALC11: " + capabilities.OpenALC11);
            System.out.println("caps.ALC_EXT_EFX = " + capabilities.ALC_EXT_EFX);


            if (capabilities.OpenALC11) {
                List<String> devices = getStringList(NULL, ALC11.ALC_ALL_DEVICES_SPECIFIER);
                if (devices == null) {
                    assertNoAlErrors();
                } else {
                    for (int i = 0; i < devices.size(); i++) {
                        System.out.println(i + ": " + devices.get(i));
                    }
                }
            }

            String defaultDeviceSpecifier = alcGetString(NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
            Assertions.assertTrue(defaultDeviceSpecifier != null);
            System.out.println("Default device: " + defaultDeviceSpecifier);

            final long context = alcCreateContext(device, (IntBuffer) null);
            EXTThreadLocalContext.alcSetThreadContext(context);
            AL.createCapabilities(capabilities);

            System.out.println("ALC_FREQUENCY: " + alcGetInteger(device, ALC_FREQUENCY) + "Hz");
            System.out.println("ALC_REFRESH: " + alcGetInteger(device, ALC_REFRESH) + "Hz");
            System.out.println("ALC_SYNC: " + (alcGetInteger(device, ALC_SYNC) == ALC_TRUE));
            System.out.println("ALC_MONO_SOURCES: " + alcGetInteger(device, ALC_MONO_SOURCES));
            System.out.println("ALC_STEREO_SOURCES: " + alcGetInteger(device, ALC_STEREO_SOURCES));

            try {
                testPlayback();
            } finally {
                alcMakeContextCurrent(NULL);
                alcDestroyContext(context);
            }
        } finally {
            alcCloseDevice(device);
        }
    }

    private void testPlayback() throws Exception {
        // generate buffers and sources
        final int buffer = alGenBuffers();
        assertNoAlErrors();

        final int source = alGenSources();
        assertNoAlErrors();

        try (Wrap<AudioSamples> samples = readableResource.read(
                Channels.newChannel(getClass().getResourceAsStream("/sample.ogg")),
                assets
        )) {
            samples.value().buffer(buffer);

            //lets loop the sound
            alSourcei(source, AL_LOOPING, AL_FALSE);
            assertNoAlErrors();

            if (false) {
                //set up source input
                alSourcei(source, AL_BUFFER, buffer);
                assertNoAlErrors();

                //play source 0
                alSourcePlay(source);
                assertNoAlErrors();

                // not sure this is correct method to wait for sound to stop
                while (alGetSourcei(source, AL_SOURCE_STATE) != AL_STOPPED) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }

                //stop source 0
                alSourceStop(source);
                assertNoAlErrors();
            } else {
                System.out.println("Queueing first buffer");
                alSourceQueueBuffers(source, buffer);
                assertNoAlErrors();
                System.out.println("Playing...");
                alSourcePlay(source);
                assertNoAlErrors();
                for (int i = 0; i < 5; i++) {
                    int processed;
                    while ((processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)) < 1) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                        }
                    }
                    System.out.println("Processed " + processed);
                    for (int k = 0; k < processed; k++) {
                        final int b = alSourceUnqueueBuffers(source);
                        samples.value().buffer(b);
                        //bufferData(buffer, info, wrap.value());
                        alSourceQueueBuffers(source, b);
                        assertNoAlErrors();
                        alSourcePlay(source);
                        assertNoAlErrors();
                    }
                }
                System.out.println("Waiting for completion...");
                while (alGetSourcei(source, AL_BUFFERS_QUEUED) > 0) {
                    if (alGetSourcei(source, AL_BUFFERS_PROCESSED) > 0) {
                        alSourceUnqueueBuffers(source);
                        assertNoAlErrors();
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                    }
                }
                System.out.println("All is done!");
            }

            //delete buffers and sources
            alDeleteSources(source);
            assertNoAlErrors();

            alDeleteBuffers(buffer);
            assertNoAlErrors();
        }
    }
}
