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

package com.github.ykiselev.lwjgl3.services.sound;

import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.SoundEffects;
import com.github.ykiselev.services.configuration.WiredValues;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSoundEffects implements SoundEffects, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private final AutoCloseable subscriptions;

    private volatile String deviceName;

    private final SoundDevice device;

    public AppSoundEffects(Services services) {
        this.services = requireNonNull(services);
        this.subscriptions = services.resolve(PersistedConfiguration.class)
                .wire(
                        new WiredValues()
                                .withString("sound.device", () -> deviceName, this::setDeviceName, true)
                                .build()
                );

        long device = alcOpenDevice(deviceName);
        if (device == NULL) {
            deviceName = null;
            device = alcOpenDevice(deviceName);
            if (device == NULL) {
                throw new IllegalArgumentException("No device found!");
            }
        }

        this.device = new SoundDevice(services, device);
    }

    private void setDeviceName(String value) {
        if (StringUtils.equalsIgnoreCase(deviceName, value)) {
            return;
        }
        logger.info("Setting device name to \"{}\"", value);
        deviceName = value;
    }

    @Override
    public void close() throws Exception {
        device.close();
        subscriptions.close();
    }
}
