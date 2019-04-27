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

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.playground.app.window.AppWindow;
import com.github.ykiselev.playground.app.window.WindowBuilder;
import com.github.ykiselev.playground.layers.AppUiLayers;
import com.github.ykiselev.playground.services.AppSprites;
import com.github.ykiselev.playground.services.Components;
import com.github.ykiselev.playground.services.assets.GameAssets;
import com.github.ykiselev.playground.services.config.AppConfig;
import com.github.ykiselev.playground.services.console.AppCommands;
import com.github.ykiselev.playground.services.console.DefaultTokenizer;
import com.github.ykiselev.playground.services.fs.AppFileSystem;
import com.github.ykiselev.playground.services.fs.ClassPathResources;
import com.github.ykiselev.playground.services.fs.DiskResources;
import com.github.ykiselev.playground.services.schedule.AppSchedule;
import com.github.ykiselev.playground.services.sound.AppSoundEffects;
import com.github.ykiselev.spi.ProgramArguments;
import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.services.Services;
import org.apache.logging.log4j.io.IoBuilder;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

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

        void run(AppWindow window, Components components) throws Exception;
    }

    public static void main(String[] args) {
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
            ).run(new ProgramArguments(args));
        } catch (Exception e) {
            LOGGER.error("Unhandled exception!", e);
            System.exit(1);
        }
    }

    private static AppLayerDelegate withGlfw(AppLayerDelegate delegate) {
        return arguments -> {
            glfwInit();
            try {
                delegate.run(arguments);
            } finally {
                glfwTerminate();
            }
        };
    }

    private static AppLayerDelegate withErrorCallback(AppLayerDelegate delegate) {
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

    private static AppLayerDelegate withStdOutLogging(AppLayerDelegate delegate) {
        return arguments -> {
            final PrintStream std = IoBuilder.forLogger("STD").buildPrintStream();
            System.setOut(std);
            System.setErr(std);
            delegate.run(arguments);
        };
    }

    private static AppLayerDelegate withServices(ServiceLayerDelegate delegate) {
        return arguments -> {
            final FileSystem fileSystem = new AppFileSystem(
                    new DiskResources(arguments.assetPaths()),
                    new ClassPathResources(Main.class.getClassLoader())
            );
            final Assets assets = GameAssets.create(fileSystem);
            final AppConfig config = new AppConfig(fileSystem);
            final Supplier<Services> supplier = () -> new Services(
                    arguments,
                    fileSystem,
                    new AppCommands(new DefaultTokenizer()),
                    config,
                    new AppSchedule(),
                    new AppUiLayers(),
                    assets,
                    new AppSprites(assets),
                    new AppSoundEffects(config),
                    new FrameInfo(60)
            );

            try (Services services = supplier.get()) {
                delegate.run(services);
            }
        };
    }

    private static ServiceLayerDelegate withWindow(WindowLayerDelegate delegate) {
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

    private static WindowLayerDelegate withGame(GameLayerDelegate delegate) {
        return (services, window) -> {
            try (Components components = new Components(services)) {
                delegate.run(window, components);
            }
        };
    }

    private static GameLayerDelegate withMainLoop() {
        return (window, components) -> {
            final AtomicBoolean exitFlag = new AtomicBoolean();
            final Services services = components.services;
            try (AutoCloseable ac = services.commands.add("quit", () -> exitFlag.set(true))) {
                LOGGER.info("Entering main loop...");
                // todo - remove that
                components.gameContainer.newGame();
                //
                while (!window.shouldClose() && !exitFlag.get()) {
                    final double t0 = glfwGetTime();
                    components.gameContainer.update();
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
