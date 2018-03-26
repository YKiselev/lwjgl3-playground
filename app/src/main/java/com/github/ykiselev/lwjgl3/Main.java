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
import com.github.ykiselev.lwjgl3.host.OnNewGameEvent;
import com.github.ykiselev.lwjgl3.host.OnShowMenuEvent;
import com.github.ykiselev.lwjgl3.host.ProgramArguments;
import com.github.ykiselev.lwjgl3.layers.AppUiLayers;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.services.AppSchedule;
import com.github.ykiselev.lwjgl3.services.MapBasedServices;
import com.github.ykiselev.lwjgl3.services.Schedule;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.lwjgl3.services.SoundEffects;
import com.github.ykiselev.lwjgl3.sound.AppSoundEffects;
import com.github.ykiselev.lwjgl3.window.AppWindow;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.io.FileSystem;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Main implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProgramArguments args;

    private boolean exitFlag;

    private Main(ProgramArguments args) {
        this.args = args;
    }

    public static void main(String[] args) {
        new ErrorCallbackApp(
                new GlfwApp(
                        new Main(
                                new ProgramArguments(args)
                        )
                )
        ).run();
    }

    @Override
    public void run() {
        try {
            final AppUiLayers layers = new AppUiLayers();
            try (Services services = new MapBasedServices()) {
                createServices(services, layers);
                try (CompositeAutoCloseable group = subscribe(services)) {
                    try (AppWindow window = new AppWindow(args.fullScreen())) {
                        GL.createCapabilities();
                        window.wireEvents(layers.events());
                        window.show();
                        services.resolve(Events.class)
                                .send(new NewGameEvent());
                        glfwSwapInterval(args.swapInterval());
                        logger.info("Entering main loop...");
                        final Schedule schedule = services.resolve(Schedule.class);
                        while (!window.shouldClose() && !exitFlag) {
                            window.checkEvents();
                            layers.draw();
                            window.swapBuffers();
                            schedule.processPendingTasks(2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
        }
    }

    private void createServices(Services services, UiLayers layers) throws IOException {
        logger.info("Creating services...");
        services.add(Events.class, new AppEvents());
        services.add(Schedule.class, new AppSchedule());
        final FileSystem fileSystem = createFileSystem();
        services.add(Assets.class, GameAssets.create(fileSystem));
        services.add(UiLayers.class, layers);
        services.add(FileSystem.class, fileSystem);
        services.add(PersistedConfiguration.class, new AppConfig(services));
        services.add(SoundEffects.class, new AppSoundEffects(services));
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
        return new SubscriptionsBuilder()
                .with(QuitGameEvent.class, this::onQuitGame)
                .with(NewGameEvent.class, new OnNewGameEvent(services))
                .with(ShowMenuEvent.class, new OnShowMenuEvent(services))
                .build(services.resolve(Events.class));
    }

    private void onQuitGame(QuitGameEvent event) {
        exitFlag = true;
    }
}