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

package com.github.ykiselev.playground.init;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.playground.app.window.AppWindow;
import com.github.ykiselev.playground.app.window.WindowBuilder;
import com.github.ykiselev.playground.layers.AppUiLayers;
import com.github.ykiselev.playground.services.AppGame;
import com.github.ykiselev.playground.services.AppMenu;
import com.github.ykiselev.playground.services.AppSprites;
import com.github.ykiselev.playground.services.assets.GameAssets;
import com.github.ykiselev.playground.services.config.AppConfig;
import com.github.ykiselev.playground.services.console.AppCommands;
import com.github.ykiselev.playground.services.console.AppConsole;
import com.github.ykiselev.playground.services.console.ConsoleFactory;
import com.github.ykiselev.playground.services.console.DefaultTokenizer;
import com.github.ykiselev.playground.services.fs.AppFileSystem;
import com.github.ykiselev.playground.services.fs.ClassPathResources;
import com.github.ykiselev.playground.services.fs.DiskResources;
import com.github.ykiselev.playground.services.schedule.AppSchedule;
import com.github.ykiselev.playground.services.sound.AppSoundEffects;
import com.github.ykiselev.spi.GameHost;
import com.github.ykiselev.spi.MonitorInfo;
import com.github.ykiselev.spi.ProgramArguments;
import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.services.Services;
import com.github.ykiselev.spi.services.SoundEffects;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.Sprites;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.services.schedule.Schedule;
import org.apache.logging.log4j.io.IoBuilder;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppComposition {

    private static final Logger logger = LoggerFactory.getLogger(AppComposition.class);

    private final Delegate delegate;

    private AppComposition(Delegate delegate) {
        this.delegate = delegate;
    }

    public void run(ProgramArguments arguments) throws Exception {
        try {
            delegate.run(new Context(ProgramArguments.class, arguments));
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
            throw e;
        }
    }

    public AppComposition withGlfw() {
        return new AppComposition(context -> {
            glfwInit();
            try {
                delegate.run(context);
            } finally {
                glfwTerminate();
            }
        });
    }

    public AppComposition withErrorCallback() {
        return new AppComposition(context -> {
            try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
                final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
                try {
                    delegate.run(context);
                } finally {
                    glfwSetErrorCallback(previous);
                }
            }
        });
    }

    public AppComposition withStdOutLogging() {
        return new AppComposition(context -> {
            final PrintStream std = IoBuilder.forLogger("STD").buildPrintStream();
            System.setOut(std);
            System.setErr(std);
            delegate.run(context);
        });
    }

    public AppComposition withFileSystem() {
        return new AppComposition(context -> {
            final ProgramArguments arguments = context.get(ProgramArguments.class);
            try (AppFileSystem fileSystem = new AppFileSystem(
                    new DiskResources(arguments.assetPaths()),
                    new ClassPathResources(AppComposition.class.getClassLoader()))
            ) {
                delegate.run(context.with(FileSystem.class, fileSystem));
            }
        });
    }

    public AppComposition withMonitorInfo() {
        return new AppComposition(context -> {
            delegate.run(
                    context.with(
                            MonitorInfo.class,
                            MonitorInfoFactory.fromIndex(
                                    context.get(ProgramArguments.class).monitor()
                            )
                    )
            );
        });
    }

    public AppComposition withAssets() {
        return new AppComposition(context -> {
            try (GameAssets assets = GameAssets.create(context.get(FileSystem.class), context.get(MonitorInfo.class))) {
                delegate.run(context.with(Assets.class, assets));
            }
        });
    }

    public AppComposition withCommands() {
        return new AppComposition(context -> {
            try (AppCommands commands = new AppCommands(new DefaultTokenizer())) {
                delegate.run(context.with(Commands.class, commands));
            }
        });
    }

    public AppComposition withPersistedConfiguration() {
        return new AppComposition(context -> {
            try (AppConfig config = new AppConfig(context.get(FileSystem.class))) {
                delegate.run(context.with(PersistedConfiguration.class, config));
            }
        });
    }

    public AppComposition withSchedule() {
        return new AppComposition(context -> {
            try (AppSchedule schedule = new AppSchedule()) {
                delegate.run(context.with(Schedule.class, schedule));
            }
        });
    }

    public AppComposition withUiLayers() {
        return new AppComposition(context -> {
            try (AppUiLayers uiLayers = new AppUiLayers()) {
                delegate.run(context.with(UiLayers.class, uiLayers));
            }
        });
    }

    public AppComposition withSprites() {
        return new AppComposition(context -> {
            try (AppSprites sprites = new AppSprites(context.get(Assets.class))) {
                delegate.run(context.with(Sprites.class, sprites));
            }
        });
    }

    public AppComposition withSoundEffects() {
        return new AppComposition(context -> {
            try (AppSoundEffects soundEffects = new AppSoundEffects(context.get(PersistedConfiguration.class))) {
                delegate.run(context.with(SoundEffects.class, soundEffects));
            }
        });
    }

    public AppComposition withWindow() {
        return new AppComposition(context -> {
            final WindowBuilder builder = new WindowBuilder()
                    .fullScreen(context.get(ProgramArguments.class).fullScreen())
                    .version(3, 3)
                    .coreProfile()
                    //.debug(true)
                    .monitor(context.get(MonitorInfo.class).monitor)
                    .dimensions(800, 600)
                    .events(context.get(UiLayers.class).events());
            try (AppWindow window = builder.build("LWJGL Playground")) {
                window.show();
                window.makeCurrent();
                delegate.run(context.with(AppWindow.class, window));
            }
        });
    }

    public AppComposition withServices() {
        return new AppComposition(context ->
                delegate.run(
                        context.with(
                                Services.class,
                                new Services(
                                        context.get(FileSystem.class),
                                        context.get(Commands.class),
                                        context.get(PersistedConfiguration.class),
                                        context.get(Schedule.class),
                                        context.get(UiLayers.class),
                                        context.get(Assets.class),
                                        context.get(Sprites.class),
                                        context.get(SoundEffects.class),
                                        context.get(AppWindow.class)
                                )
                        )
                ));
    }

    public AppComposition withGameHost() {
        return new AppComposition(context ->
                delegate.run(
                        context.with(
                                GameHost.class,
                                new GameHost(
                                        context.get(ProgramArguments.class),
                                        context.get(Services.class),
                                        new FrameInfo(60)
                                )
                        )
                ));
    }

    public AppComposition withConsole() {
        return new AppComposition(context -> {
            try (AppConsole console = ConsoleFactory.create(context.get(Services.class))) {
                delegate.run(context.with(AppConsole.class, console));
            }
        });
    }

    public AppComposition withMenu() {
        return new AppComposition(context -> {
            try (AppMenu menu = new AppMenu(context.get(Services.class))) {
                delegate.run(context.with(AppMenu.class, menu));
            }
        });
    }

    public AppComposition withGame() {
        return new AppComposition(context -> {
            try (AppGame game = new AppGame(context.get(GameHost.class))) {
                delegate.run(context.with(AppGame.class, game));
            }
        });
    }

    public static AppComposition withMainLoop() {
        return new AppComposition(context -> {
            final AtomicBoolean exitFlag = new AtomicBoolean();
            final Services services = context.get(Services.class);
            final AppWindow window = context.get(AppWindow.class);
            final GameHost host = context.get(GameHost.class);
            final AppGame game = context.get(AppGame.class);
            try (AutoCloseable ac = services.commands.add("quit", () -> {
                logger.info("Exiting app...");
                exitFlag.set(true);
            })) {
                logger.info("Entering main loop...");
                window.makeCurrent();
                glfwSwapInterval(host.arguments.swapInterval());
                // todo - remove that (why?)
                services.commands.execute("new-game");
                //
                while (!window.shouldClose() && !exitFlag.get()) {
                    final double t0 = glfwGetTime();
                    window.makeCurrent();
                    game.update();
                    window.checkEvents();
                    services.uiLayers.draw();
                    window.swapBuffers();
                    services.schedule.processPendingTasks(2);
                    final double t1 = glfwGetTime();
                    host.frameInfo.add((t1 - t0) * 1000.0);
                }
                services.persistedConfiguration.persist();
            }
        });
    }
}
