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

import static java.util.Objects.requireNonNull;
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
        final class GlfwLayer implements AppLayerDelegate {

            private final AppLayerDelegate delegate;

            private GlfwLayer(AppLayerDelegate delegate) {
                this.delegate = delegate;
            }

            @Override
            public void run(ProgramArguments arguments) throws Exception {
                glfwInit();
                try {
                    delegate.run(arguments);
                } finally {
                    glfwTerminate();
                }
            }
        }

        final class ErrorCallbackLayer implements AppLayerDelegate {

            private final AppLayerDelegate delegate;

            private ErrorCallbackLayer(AppLayerDelegate delegate) {
                this.delegate = delegate;
            }

            @Override
            public void run(ProgramArguments arguments) throws Exception {
                try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
                    final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
                    try {
                        delegate.run(arguments);
                    } finally {
                        glfwSetErrorCallback(previous);
                    }
                }
            }
        }

        final class StdOutLoggingLayer implements AppLayerDelegate {

            private final AppLayerDelegate delegate;

            private StdOutLoggingLayer(AppLayerDelegate delegate) {
                this.delegate = delegate;
            }

            @Override
            public void run(ProgramArguments arguments) throws Exception {
                final PrintStream std = IoBuilder.forLogger("STD").buildPrintStream();
                System.setOut(std);
                System.setErr(std);
                delegate.run(arguments);
            }
        }

        final class ServiceLayer implements AppLayerDelegate {

            private final ServiceLayerDelegate delegate;

            private ServiceLayer(ServiceLayerDelegate delegate) {
                this.delegate = requireNonNull(delegate);
            }

            @Override
            public void run(ProgramArguments arguments) throws Exception {
                try (Services services = create(arguments)) {
                    delegate.run(services);
                }
            }

            private Services create(ProgramArguments args) {
                final FileSystem fileSystem = new AppFileSystem(
                        new DiskResources(args.assetPaths()),
                        new ClassPathResources(getClass().getClassLoader())
                );
                final Assets assets = GameAssets.create(fileSystem);
                final AppConfig config = new AppConfig(fileSystem);
                return new Services(
                        args,
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
            }
        }

        final class WindowLayer implements ServiceLayerDelegate {

            private final WindowLayerDelegate delegate;

            private WindowLayer(WindowLayerDelegate delegate) {
                this.delegate = delegate;
            }

            @Override
            public void run(Services services) throws Exception {
                try (AppWindow window = createWindow(services)) {
                    window.show();
                    glfwSwapInterval(services.arguments.swapInterval());
                    delegate.run(services, window);
                }
            }

            private AppWindow createWindow(Services services) {
                return new WindowBuilder()
                        .fullScreen(services.arguments.fullScreen())
                        .version(3, 3)
                        .coreProfile()
                        .debug(true)
                        .primaryMonitor()
                        .dimensions(800, 600)
                        .events(services.uiLayers.events())
                        .build("LWJGL Playground");
            }
        }

        final class GameLayer implements WindowLayerDelegate {

            private GameLayerDelegate delegate;

            private GameLayer(GameLayerDelegate delegate) {
                this.delegate = delegate;
            }

            @Override
            public void run(Services services, AppWindow window) throws Exception {
                try (Components components = new Components(services)) {
                    delegate.run(window, components);
                }
            }
        }

        final class MainLoop implements GameLayerDelegate {

            private volatile boolean exitFlag;

            @Override
            public void run(AppWindow window, Components components) throws Exception {
                final Services services = components.services;
                try (AutoCloseable ac = services.commands.add("quit", () -> exitFlag = true)) {
                    LOGGER.info("Entering main loop...");
                    // todo - remove that
                    components.gameContainer.newGame();
                    //
                    while (!window.shouldClose() && !exitFlag) {
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
            }
        }

        try {
            new GlfwLayer(
                    new ErrorCallbackLayer(
                            new StdOutLoggingLayer(
                                    new ServiceLayer(
                                            new WindowLayer(
                                                    new GameLayer(
                                                            new MainLoop()
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
}
