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

package com.github.ykiselev.playground.services.sound;

import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;
import java.util.List;

import static com.github.ykiselev.openal.Errors.assertNoAlErrors;
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
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class OpenAlSoundDevice implements SoundDevice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final long device;

    private final long context;

    private int effectsLevel;

    private int musicLevel;

    private final AutoCloseable subscriptions;

    OpenAlSoundDevice(PersistedConfiguration persistedConfiguration, long device) {
        this.device = device;
        this.subscriptions = persistedConfiguration.wire()
                .withInt("sound.effects.level", () -> effectsLevel, this::setEffectsLevel, true)
                .withInt("sound.music.level", () -> musicLevel, v -> musicLevel = v, true)
                .build();

        final ALCCapabilities capabilities = createCapabilities(device);
        if (!capabilities.OpenALC10) {
            throw new IllegalArgumentException("OpenAL 1.0 is not supported!");
        }
        logger.info("Is OpenALC11 ? {}, has ALC_EXT_EFX ? {}", capabilities.OpenALC11, capabilities.ALC_EXT_EFX);

        if (capabilities.OpenALC11) {
            List<String> devices = getStringList(NULL, ALC11.ALC_ALL_DEVICES_SPECIFIER);
            if (devices == null) {
                assertNoAlErrors();
            } else {
                logger.info("All devices: {}", devices);
            }
        }
        final String defaultDeviceSpecifier = alcGetString(NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        logger.info("Default device: {}", defaultDeviceSpecifier);
        final String deviceSpecifier = alcGetString(device, ALC10.ALC_DEVICE_SPECIFIER);
        logger.info("Device: {}", deviceSpecifier);

        context = alcCreateContext(device, (IntBuffer) null);
        EXTThreadLocalContext.alcSetThreadContext(context);
        AL.createCapabilities(capabilities);

        logger.info(
                "ALC_FREQUENCY={} Hz, ALC_REFRESH={} Hz, ALC_SYNC={}, ALC_MONO_SOURCES={}, ALC_STEREO_SOURCES={}",
                alcGetInteger(device, ALC_FREQUENCY),
                alcGetInteger(device, ALC_REFRESH),
                (alcGetInteger(device, ALC_SYNC) == ALC_TRUE),
                alcGetInteger(device, ALC_MONO_SOURCES),
                alcGetInteger(device, ALC_STEREO_SOURCES)
        );
    }

    private void setEffectsLevel(int value) {
        logger.info("Setting sound level to {}", value);
        effectsLevel = value;
    }

    @Override
    public void close() throws Exception {
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
        subscriptions.close();
    }
}