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
import com.github.ykiselev.io.FileSystem;
import com.github.ykiselev.lwjgl3.app.ErrorCallbackApp;
import com.github.ykiselev.lwjgl3.app.GlfwApp;
import com.github.ykiselev.lwjgl3.assets.GameAssets;
import com.github.ykiselev.lwjgl3.config.AppConfig;
import com.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import com.github.ykiselev.lwjgl3.events.AppEvents;
import com.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
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
import com.github.ykiselev.lwjgl3.window.AppWindow;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Main {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProgramArguments args;

    private volatile boolean exitFlag;

    private Main(ProgramArguments args) {
        this.args = requireNonNull(args);
    }

    public static void main(String[] args) {
        new Main(
                new ProgramArguments(args)
        ).run();
    }

    private void run() {
        try {
            new ErrorCallbackApp(
                    new GlfwApp(this::mainLoop)
            ).call();
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
        }
    }

    private Void mainLoop() throws Exception {
        try (Services services = new MapBasedServices();
             AutoCloseable g1 = registerServices(services);
             AutoCloseable g2 = subscribe(services);
             AppWindow window = new AppWindow(args.fullScreen())
        ) {
            GL.createCapabilities();
            final UiLayers layers = services.resolve(UiLayers.class);
            window.wireEvents(layers.events());
            window.show();
            services.resolve(Events.class)
                    .fire(new NewGameEvent());
            glfwSwapInterval(args.swapInterval());
            logger.info("Entering main loop...");
            final Schedule schedule = services.resolve(Schedule.class);
            while (!window.shouldClose() && !exitFlag) {
                window.checkEvents();
                layers.draw();
                window.swapBuffers();
                schedule.processPendingTasks(2);
            }
            return null;
        }
    }

    private AutoCloseable registerServices(Services services) {
        logger.info("Creating services...");
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

    private AutoCloseable subscribe(Services services) {
        final Events events = services.resolve(Events.class);
        final GameEvents gameEvents = new GameEvents(services);
        final MenuEvents menuEvents = new MenuEvents(services);
        return new SubscriptionsBuilder(events)
                .with(QuitGameEvent.class, this::onQuitGame)
                .with(ShowMenuEvent.class, menuEvents::onShowMenuEvent)
                .with(NewGameEvent.class, gameEvents::onNewGameEvent)
                .build()
                .and(gameEvents)
                .and(menuEvents);
    }

    private QuitGameEvent onQuitGame(QuitGameEvent event) {
        exitFlag = true;
        return null;
    }
}
