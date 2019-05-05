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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private interface Delegate {

        void run(Context services) throws Exception;
    }

    public static void main(String[] args) throws Exception {
        new Main().run(new ProgramArguments(args));
    }

    private void run(ProgramArguments arguments) throws Exception {
        try {
            withGlfw(
                    withErrorCallback(
                            withStdOutLogging(
                                    withFileSystem(
                                            withMonitorInfo(
                                                    withAssets(
                                                            withCommands(
                                                                    withPersistedConfiguration(
                                                                            withSchedule(
                                                                                    withUiLayers(
                                                                                            withSprites(
                                                                                                    withSoundEffects(
                                                                                                            withWindow(
                                                                                                                    withServices(
                                                                                                                            withGameHost(
                                                                                                                                    withConsole(
                                                                                                                                            withMenu(
                                                                                                                                                    withGame(
                                                                                                                                                            withMainLoop()
                                                                                                                                                    )
                                                                                                                                            )
                                                                                                                                    )
                                                                                                                            )
                                                                                                                    )
                                                                                                            )
                                                                                                    )
                                                                                            )
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            ).run(new Context(ProgramArguments.class, arguments));
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
            throw e;
        }
    }

    private Delegate withGlfw(Delegate delegate) {
        return context -> {
            glfwInit();
            try {
                delegate.run(context);
            } finally {
                glfwTerminate();
            }
        };
    }

    private Delegate withErrorCallback(Delegate delegate) {
        return context -> {
            try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
                final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
                try {
                    delegate.run(context);
                } finally {
                    glfwSetErrorCallback(previous);
                }
            }
        };
    }

    private Delegate withStdOutLogging(Delegate delegate) {
        return context -> {
            final PrintStream std = IoBuilder.forLogger("STD").buildPrintStream();
            System.setOut(std);
            System.setErr(std);
            delegate.run(context);
        };
    }

    private Delegate withFileSystem(Delegate delegate) {
        return context -> {
            final ProgramArguments arguments = context.get(ProgramArguments.class);
            try (AppFileSystem fileSystem = new AppFileSystem(
                    new DiskResources(arguments.assetPaths()),
                    new ClassPathResources(Main.class.getClassLoader()))
            ) {
                delegate.run(context.with(FileSystem.class, fileSystem));
            }
        };
    }

    private Delegate withMonitorInfo(Delegate delegate) {
        return context -> {
            final long monitor = getMonitor(context.get(ProgramArguments.class).monitor());
            final MonitorInfo monitorInfo = getMonitorInfo(monitor);
            delegate.run(context.with(MonitorInfo.class, monitorInfo));
        };
    }

    private Delegate withAssets(Delegate delegate) {
        return context -> {
            try (GameAssets assets = GameAssets.create(context.get(FileSystem.class), context.get(MonitorInfo.class))) {
                delegate.run(context.with(Assets.class, assets));
            }
        };
    }

    private Delegate withCommands(Delegate delegate) {
        return context -> {
            try (AppCommands commands = new AppCommands(new DefaultTokenizer())) {
                delegate.run(context.with(Commands.class, commands));
            }
        };
    }

    private Delegate withPersistedConfiguration(Delegate delegate) {
        return context -> {
            try (AppConfig config = new AppConfig(context.get(FileSystem.class))) {
                delegate.run(context.with(PersistedConfiguration.class, config));
            }
        };
    }

    private Delegate withSchedule(Delegate delegate) {
        return context -> {
            try (AppSchedule schedule = new AppSchedule()) {
                delegate.run(context.with(Schedule.class, schedule));
            }
        };
    }

    private Delegate withUiLayers(Delegate delegate) {
        return context -> {
            try (AppUiLayers uiLayers = new AppUiLayers()) {
                delegate.run(context.with(UiLayers.class, uiLayers));
            }
        };
    }

    private Delegate withSprites(Delegate delegate) {
        return context -> {
            try (AppSprites sprites = new AppSprites(context.get(Assets.class))) {
                delegate.run(context.with(Sprites.class, sprites));
            }
        };
    }

    private Delegate withSoundEffects(Delegate delegate) {
        return context -> {
            try (AppSoundEffects soundEffects = new AppSoundEffects(context.get(PersistedConfiguration.class))) {
                delegate.run(context.with(SoundEffects.class, soundEffects));
            }
        };
    }

    private Delegate withWindow(Delegate delegate) {
        return context -> {
            final WindowBuilder builder = new WindowBuilder()
                    .fullScreen(context.get(ProgramArguments.class).fullScreen())
                    .version(3, 3)
                    .coreProfile()
                    .debug(true)
                    .monitor(context.get(MonitorInfo.class).monitor)
                    .dimensions(800, 600)
                    .events(context.get(UiLayers.class).events());
            try (AppWindow window = builder.build("LWJGL PLayground")) {
                window.show();
                window.makeCurrent();
                delegate.run(context.with(AppWindow.class, window));
            }
        };
    }

    private Delegate withServices(Delegate delegate) {
        return context ->
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
                );
    }

    private Delegate withGameHost(Delegate delegate) {
        return context ->
                delegate.run(
                        context.with(
                                GameHost.class,
                                new GameHost(
                                        context.get(ProgramArguments.class),
                                        context.get(Services.class),
                                        new FrameInfo(60)
                                )
                        )
                );
    }

    private Delegate withConsole(Delegate delegate) {
        return context -> {
            try (AppConsole console = ConsoleFactory.create(context.get(Services.class))) {
                delegate.run(context.with(AppConsole.class, console));
            }
        };
    }

    private Delegate withMenu(Delegate delegate) {
        return context -> {
            try (AppMenu menu = new AppMenu(context.get(Services.class))) {
                delegate.run(context.with(AppMenu.class, menu));
            }
        };
    }

    private Delegate withGame(Delegate delegate) {
        return context -> {
            try (AppGame game = new AppGame(context.get(GameHost.class))) {
                delegate.run(context.with(AppGame.class, game));
            }
        };
    }

    private Delegate withMainLoop() {
        return context -> {
            final AtomicBoolean exitFlag = new AtomicBoolean();
            final Services services = context.get(Services.class);
            final AppWindow window = context.get(AppWindow.class);
            final GameHost host = context.get(GameHost.class);
            final AppGame game = context.get(AppGame.class);
            try (AutoCloseable ac = services.commands.add("quit", () -> exitFlag.set(true))) {
                logger.info("Entering main loop...");
                window.makeCurrent();
                glfwSwapInterval(host.arguments.swapInterval());
                // todo - remove that
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

    private static final class Context {

        static final class Node {

            final Class<?> key;

            final Object value;

            final Node next;

            Node(Class<?> key, Object value, Node next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        private final Node head;

        Context(Node head) {
            this.head = head;
        }

        Context(Class<?> key, Object value) {
            this(new Node(key, value, null));
        }

        Context with(Class<?> key, Object value) {
            return new Context(new Node(key, value, head));
        }

        @SuppressWarnings("unchecked")
        <T> T get(Class<T> clazz) {
            Node node = this.head;
            while (node != null) {
                if (clazz.equals(node.key)) {
                    return (T) requireNonNull(node.value);
                }
                node = node.next;
            }
            throw new IllegalArgumentException("Not found: " + clazz);
        }
    }
}
