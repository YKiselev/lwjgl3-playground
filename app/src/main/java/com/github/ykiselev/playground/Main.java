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
import com.github.ykiselev.spi.api.Updateable;
import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.services.Services;
import com.github.ykiselev.spi.services.SoundEffects;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.Sprites;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.services.schedule.Schedule;
import org.apache.logging.log4j.io.IoBuilder;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.nio.FloatBuffer;
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

        void run(ProgramArguments arguments, FileSystem fileSystem, Commands commands, PersistedConfiguration configuration,
                 Schedule schedule, UiLayers uiLayers, Assets assets, Sprites sprites, SoundEffects soundEffects) throws Exception;
    }

    private interface WindowLayerDelegate {

        void run(ProgramArguments arguments, Services services, AppWindow window) throws Exception;
    }

    interface GameLayerDelegate {

        void run(AppWindow window, GameHost host, Updateable game) throws Exception;
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
            final long monitor = getMonitor(arguments.monitor());
            final MonitorInfo monitorInfo = getMonitorInfo(monitor);
            try (AppFileSystem fileSystem = new AppFileSystem(
                    new DiskResources(arguments.assetPaths()),
                    new ClassPathResources(Main.class.getClassLoader()));
                 GameAssets assets = GameAssets.create(fileSystem, monitorInfo);
                 AppCommands commands = new AppCommands(new DefaultTokenizer());
                 AppConfig config = new AppConfig(fileSystem);
                 AppSchedule schedule = new AppSchedule();
                 AppUiLayers uiLayers = new AppUiLayers();
                 AppSprites sprites = new AppSprites(assets);
                 AppSoundEffects soundEffects = new AppSoundEffects(config)
            ) {
                delegate.run(
                        arguments,
                        fileSystem,
                        commands,
                        config,
                        schedule,
                        uiLayers,
                        assets,
                        sprites,
                        soundEffects
                );
            }
        };
    }

    private ServiceLayerDelegate withWindow(WindowLayerDelegate delegate) {
        return (arguments, fileSystem, commands, configuration, schedule, uiLayers, assets, sprites, soundEffects) -> {
            final WindowBuilder builder = new WindowBuilder()
                    .fullScreen(arguments.fullScreen())
                    .version(3, 3)
                    .coreProfile()
                    .debug(true)
                    .monitor(arguments.monitor())
                    .dimensions(800, 600)
                    .events(uiLayers.events());
            try (AppWindow window = builder.build("LWJGL PLayground")) {
                window.show();
                glfwSwapInterval(arguments.swapInterval());
                delegate.run(
                        arguments,
                        new Services(
                                fileSystem,
                                commands,
                                configuration,
                                schedule,
                                uiLayers,
                                assets,
                                sprites,
                                soundEffects,
                                window
                        ),
                        window
                );
            }
        };
    }

    private WindowLayerDelegate withGame(GameLayerDelegate delegate) {
        return (arguments, services, window) -> {
            final GameHost gameHost = new GameHost(arguments, services, new FrameInfo(60));
            try (AppConsole console = ConsoleFactory.create(services);
                 AppMenu menu = new AppMenu(services);
                 AppGame game = new AppGame(gameHost)) {
                delegate.run(window, gameHost, game);
            }
        };
    }

    private GameLayerDelegate withMainLoop() {
        return (window, host, game) -> {
            final AtomicBoolean exitFlag = new AtomicBoolean();
            try (AutoCloseable ac = host.services.commands.add("quit", () -> exitFlag.set(true))) {
                logger.info("Entering main loop...");
                // todo - remove that
                host.services.commands.execute("new-game");
                //
                while (!window.shouldClose() && !exitFlag.get()) {
                    final double t0 = glfwGetTime();
                    window.makeCurrent();
                    game.update();
                    window.checkEvents();
                    host.services.uiLayers.draw();
                    window.swapBuffers();
                    host.services.schedule.processPendingTasks(2);
                    final double t1 = glfwGetTime();
                    host.frameInfo.add((t1 - t0) * 1000.0);
                }
                host.services.persistedConfiguration.persist();
            }
        };
    }

    private long getMonitor(int index) {
        if (index < 0) {
            return GLFW.glfwGetPrimaryMonitor();
        }
        final PointerBuffer monitors = GLFW.glfwGetMonitors();
        if (monitors == null || monitors.remaining() <= index) {
            return GLFW.glfwGetPrimaryMonitor();
        }
        return monitors.get(index);
    }

    private MonitorInfo getMonitorInfo(long monitor) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer xs = stack.mallocFloat(1);
            final FloatBuffer ys = stack.mallocFloat(1);
            GLFW.glfwGetMonitorContentScale(monitor, xs, ys);
            return new MonitorInfo(monitor, xs.get(0), ys.get(0));
        }
    }

}
