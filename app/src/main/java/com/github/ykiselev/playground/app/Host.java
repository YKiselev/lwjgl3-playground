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

package com.github.ykiselev.playground.app;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.playground.events.AppEvents;
import com.github.ykiselev.playground.host.ConsoleEvents;
import com.github.ykiselev.playground.host.GameEvents;
import com.github.ykiselev.playground.host.MenuEvents;
import com.github.ykiselev.playground.host.ProgramArguments;
import com.github.ykiselev.playground.layers.AppUiLayers;
import com.github.ykiselev.playground.services.MapBasedServices;
import com.github.ykiselev.playground.services.assets.GameAssets;
import com.github.ykiselev.playground.services.config.AppConfig;
import com.github.ykiselev.playground.services.console.AppCommands;
import com.github.ykiselev.playground.services.console.DefaultTokenizer;
import com.github.ykiselev.playground.services.fs.AppFileSystem;
import com.github.ykiselev.playground.services.fs.ClassPathResources;
import com.github.ykiselev.playground.services.fs.DiskResources;
import com.github.ykiselev.playground.services.schedule.AppSchedule;
import com.github.ykiselev.playground.services.sound.AppSoundEffects;
import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.ServiceGroupBuilder;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.SoundEffects;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.services.schedule.Schedule;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Creates service registry and registers basic services and event handlers.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Host implements Runnable {

    private final ProgramArguments args;

    private final Consumer<Services> delegate;

    public Host(ProgramArguments args, Consumer<Services> delegate) {
        this.args = requireNonNull(args);
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public void run() {
        final Services services = new MapBasedServices();
        final CompositeAutoCloseable ac = new CompositeAutoCloseable(services)
                .and(registerServices(services))
                .with(new MenuEvents(services))
                .with(new ConsoleEvents(services))
                .with(new GameEvents(services))
                .reverse();
        try (ac) {
            delegate.accept(services);
        }
    }

    private CompositeAutoCloseable registerServices(Services services) {
        final FileSystem fileSystem = createFileSystem();
        return new ServiceGroupBuilder(services)
                .add(FileSystem.class, fileSystem)
                .add(Events.class, new AppEvents())
                .add(Commands.class, new AppCommands(new DefaultTokenizer()))
                .add(PersistedConfiguration.class, new AppConfig(services))
                .add(Schedule.class, new AppSchedule())
                .add(UiLayers.class, new AppUiLayers())
                .add(Assets.class, GameAssets.create(fileSystem))
                .add(SoundEffects.class, new AppSoundEffects(services))
                .build();
    }

    private FileSystem createFileSystem() {
        return new AppFileSystem(
                new DiskResources(args.assetPaths()),
                new ClassPathResources(getClass().getClassLoader())
        );
    }
}
