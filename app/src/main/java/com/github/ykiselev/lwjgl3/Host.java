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

package com.github.ykiselev.lwjgl3;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.io.FileSystem;
import com.github.ykiselev.lwjgl3.assets.GameAssets;
import com.github.ykiselev.lwjgl3.config.AppConfig;
import com.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import com.github.ykiselev.lwjgl3.events.AppEvents;
import com.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import com.github.ykiselev.lwjgl3.fs.AppFileSystem;
import com.github.ykiselev.lwjgl3.fs.ClassPathResources;
import com.github.ykiselev.lwjgl3.fs.DiskResources;
import com.github.ykiselev.lwjgl3.host.GameEvents;
import com.github.ykiselev.lwjgl3.host.MenuEvents;
import com.github.ykiselev.lwjgl3.host.ProgramArguments;
import com.github.ykiselev.lwjgl3.layers.AppUiLayers;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.services.MapBasedServices;
import com.github.ykiselev.lwjgl3.services.ServiceGroupBuilder;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.lwjgl3.services.SoundEffects;
import com.github.ykiselev.lwjgl3.services.schedule.AppSchedule;
import com.github.ykiselev.lwjgl3.services.schedule.Schedule;
import com.github.ykiselev.lwjgl3.sound.AppSoundEffects;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Creates service registry and registers basic services and event handlers.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Host implements Runnable {

    private final ProgramArguments args;

    private final Function<Services, Runnable> factory;

    public Host(ProgramArguments args, Function<Services, Runnable> factory) {
        this.args = requireNonNull(args);
        this.factory = requireNonNull(factory);
    }

    @Override
    public void run() {
        final Services services = new MapBasedServices();
        try (CompositeAutoCloseable ac = new CompositeAutoCloseable(services)
                .and(registerServices(services))
                .and(subscribe(services))
                .reverse()
        ) {
            factory.apply(services).run();
        }
    }

    private CompositeAutoCloseable registerServices(Services services) {
        final FileSystem fileSystem = createFileSystem();
        return new ServiceGroupBuilder(services)
                .add(Schedule.class, new AppSchedule())
                .add(Events.class, new AppEvents())
                .add(UiLayers.class, new AppUiLayers())
                .add(Assets.class, GameAssets.create(fileSystem))
                .add(FileSystem.class, fileSystem)
                .add(PersistedConfiguration.class, new AppConfig(services))
                .add(SoundEffects.class, new AppSoundEffects(services))
                .build();
    }

    private FileSystem createFileSystem() {
        return new AppFileSystem(
                args.home(),
                Arrays.asList(
                        new DiskResources(args.assetPaths()),
                        new ClassPathResources(getClass().getClassLoader())
                )
        );
    }

    private CompositeAutoCloseable subscribe(Services services) {
        final GameEvents gameEvents = new GameEvents(services);
        final MenuEvents menuEvents = new MenuEvents(services);
        return new SubscriptionsBuilder(services.resolve(Events.class))
                .with(ShowMenuEvent.class, menuEvents::onShowMenuEvent)
                .with(NewGameEvent.class, gameEvents::onNewGameEvent)
                .build()
                .and(gameEvents)
                .and(menuEvents);
    }
}
