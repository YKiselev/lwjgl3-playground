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

package com.github.ykiselev.spi.services;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.spi.ProgramArguments;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.Sprites;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.services.schedule.Schedule;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 24.04.2019
 */
public final class Services implements AutoCloseable {

    public final ProgramArguments arguments;

    public final FileSystem fileSystem;

    public final Commands commands;

    public final PersistedConfiguration persistedConfiguration;

    public final Schedule schedule;

    public final UiLayers uiLayers;

    public final Assets assets;

    public final Sprites sprites;

    public final SoundEffects soundEffects;

    public final FrameInfo frameInfo;

    public Services(ProgramArguments arguments, FileSystem fileSystem, Commands commands, PersistedConfiguration persistedConfiguration, Schedule schedule, UiLayers uiLayers, Assets assets, Sprites sprites, SoundEffects soundEffects, FrameInfo frameInfo) {
        this.arguments = requireNonNull(arguments);
        this.fileSystem = requireNonNull(fileSystem);
        this.commands = requireNonNull(commands);
        this.persistedConfiguration = requireNonNull(persistedConfiguration);
        this.schedule = requireNonNull(schedule);
        this.uiLayers = requireNonNull(uiLayers);
        this.assets = requireNonNull(assets);
        this.sprites = requireNonNull(sprites);
        this.soundEffects = requireNonNull(soundEffects);
        this.frameInfo = requireNonNull(frameInfo);
    }

    @Override
    public void close() {
        Closeables.closeAll(fileSystem, commands, persistedConfiguration,
                schedule, uiLayers, assets, sprites, soundEffects);
    }
}
