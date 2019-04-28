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

package com.github.ykiselev.playground;

import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.playground.app.window.AppWindow;
import com.github.ykiselev.playground.app.window.WindowBuilder;
import com.github.ykiselev.playground.layers.AppUiLayers;
import com.github.ykiselev.playground.services.AppGame;
import com.github.ykiselev.playground.services.AppMenuFactory;
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
import com.github.ykiselev.spi.ProgramArguments;
import com.github.ykiselev.spi.api.Updateable;
import com.github.ykiselev.spi.services.Services;
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
public final class Main {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private interface AppLayerDelegate {

        void run(ProgramArguments arguments) throws Exception;
    }

    private interface ServiceLayerDelegate {

        void run(Services services) throws Exception;
    }

    private interface WindowLayerDelegate {

        void run(Services services, AppWindow window) throws Exception;
    }

    interface GameLayerDelegate {

        void run(AppWindow window, Services services, Updateable game) throws Exception;
    }

    public static void main(String[] args) throws Exception {
        new Main().run(new ProgramArguments(args));
    }

    private void run(ProgramArguments arguments) throws Exception {
        try {
            withGlfw(
                    withErrorCallback(
                            withStdOutLogging(
                                    withServices(
                                            withWindow(
                                                    withGame(
                                                            withMainLoop()
                                                    )
                                            )
                                    )
                            )
                    )
            ).run(arguments);
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
            throw e;
        }
    }

    private AppLayerDelegate withGlfw(AppLayerDelegate delegate) {
        return arguments -> {
            glfwInit();
            try {
                delegate.run(arguments);
            } finally {
                glfwTerminate();
            }
        };
    }

    private AppLayerDelegate withErrorCallback(AppLayerDelegate delegate) {
        return arguments -> {
            try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
                final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
                try {
                    delegate.run(arguments);
                } finally {
                    glfwSetErrorCallback(previous);
                }
            }
        };
    }

    private AppLayerDelegate withStdOutLogging(AppLayerDelegate delegate) {
        return arguments -> {
            final PrintStream std = IoBuilder.forLogger("STD").buildPrintStream();
            System.setOut(std);
            System.setErr(std);
            delegate.run(arguments);
        };
    }

    private AppLayerDelegate withServices(ServiceLayerDelegate delegate) {
        return arguments -> {
            try (AppFileSystem fileSystem = new AppFileSystem(
                    new DiskResources(arguments.assetPaths()),
                    new ClassPathResources(Main.class.getClassLoader()));
                 GameAssets assets = GameAssets.create(fileSystem);
                 AppCommands commands = new AppCommands(new DefaultTokenizer());
                 AppConfig config = new AppConfig(fileSystem);
                 AppSchedule schedule = new AppSchedule();
                 AppUiLayers uiLayers = new AppUiLayers();
                 AppSprites sprites = new AppSprites(assets);
                 AppSoundEffects soundEffects = new AppSoundEffects(config)
            ) {
                delegate.run(
                        new Services(
                                arguments,
                                fileSystem,
                                commands,
                                config,
                                schedule,
                                uiLayers,
                                assets,
                                sprites,
                                soundEffects,
                                new FrameInfo(60)
                        )
                );
            }
        };
    }

    private ServiceLayerDelegate withWindow(WindowLayerDelegate delegate) {
        return services -> {
            final WindowBuilder builder = new WindowBuilder()
                    .fullScreen(services.arguments.fullScreen())
                    .version(3, 3)
                    .coreProfile()
                    .debug(true)
                    .primaryMonitor()
                    .dimensions(800, 600)
                    .events(services.uiLayers.events());
            try (AppWindow window = builder.build("LWJGL PLayground")) {
                window.show();
                glfwSwapInterval(services.arguments.swapInterval());
                delegate.run(services, window);
            }
        };
    }

    private WindowLayerDelegate withGame(GameLayerDelegate delegate) {
        return (services, window) -> {
            try (AppConsole console = ConsoleFactory.create(services);
                 AppMenuFactory menuFactory = new AppMenuFactory(services);
                 AppGame game = new AppGame(services)) {
                delegate.run(window, services, game);
            }
        };
    }

    private GameLayerDelegate withMainLoop() {
        return (window, services, game) -> {
            final AtomicBoolean exitFlag = new AtomicBoolean();
            try (AutoCloseable ac = services.commands.add("quit", () -> exitFlag.set(true))) {
                logger.info("Entering main loop...");
                // todo - remove that
                services.commands.execute("new-game");
                //
                while (!window.shouldClose() && !exitFlag.get()) {
                    final double t0 = glfwGetTime();
                    game.update();
                    window.checkEvents();
                    services.uiLayers.draw();
                    window.swapBuffers();
                    services.schedule.processPendingTasks(2);
                    final double t1 = glfwGetTime();
                    services.frameInfo.add((t1 - t0) * 1000.0);
                }
                services.persistedConfiguration.persist();
            }
        };
    }
}
