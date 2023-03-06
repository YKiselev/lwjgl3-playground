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
import com.github.ykiselev.playground.init.*;
import com.github.ykiselev.playground.layers.AppUiLayers;
import com.github.ykiselev.playground.services.AppContext;
import com.github.ykiselev.playground.services.AppMenu;
import com.github.ykiselev.playground.services.AppSprites;
import com.github.ykiselev.playground.services.GameBootstrap;
import com.github.ykiselev.playground.services.assets.GameAssets;
import com.github.ykiselev.playground.services.config.AppConfig;
import com.github.ykiselev.playground.services.console.AppCommands;
import com.github.ykiselev.playground.services.console.ConsoleFactory;
import com.github.ykiselev.playground.services.console.DefaultTokenizer;
import com.github.ykiselev.playground.services.fs.AppFileSystem;
import com.github.ykiselev.playground.services.fs.ClassPathResources;
import com.github.ykiselev.playground.services.fs.DiskResources;
import com.github.ykiselev.playground.services.schedule.AppSchedule;
import com.github.ykiselev.playground.services.sound.AppSoundEffects;
import com.github.ykiselev.spi.MonitorInfo;
import com.github.ykiselev.spi.ProgramArguments;
import com.github.ykiselev.spi.services.layers.UiLayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Main {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProgramArguments arguments;

    private Main(ProgramArguments arguments) {
        this.arguments = Objects.requireNonNull(arguments);
    }

    public static void main(String[] args) {
        new Main(new ProgramArguments(args)).run();
    }

    private void run() {
        try (var glfw = new GlfwBootstrap();
             var stdOut = new StdOutBootstrap();
             var errCallback = new ErrorCallbackBootstrap();
             var fileSystem = new AppFileSystem(
                     new DiskResources(arguments.assetPaths()),
                     new ClassPathResources(Main.class.getClassLoader())
             )
        ) {
            var monitorInfo = MonitorInfoFactory.fromIndex(arguments.monitor());
            try (var assets = GameAssets.create(fileSystem, monitorInfo);
                 var commands = new AppCommands(new DefaultTokenizer());
                 var config = new AppConfig(fileSystem);
                 var schedule = new AppSchedule();
                 var uiLayers = new AppUiLayers();
                 var soundEffects = new AppSoundEffects(config)
            ) {
                try (AppWindow window = createWindow(monitorInfo, uiLayers)) {
                    window.show();
                    window.makeCurrent();
                    glfwSwapInterval(arguments.swapInterval());
                    try (var spriteBatch = AppSprites.createBatch(assets)) {
                        var appContext = new AppContext(arguments, fileSystem, commands, config,
                                schedule, uiLayers, assets, spriteBatch, soundEffects, window, new FrameInfo(60));
                        try (var console = ConsoleFactory.create(config, commands, uiLayers, spriteBatch, assets);
                             var menu = new AppMenu(assets, spriteBatch, config, commands, uiLayers, schedule);
                             var game = new GameBootstrap(appContext)) {
                            runMainLoop(appContext,
                                    new FrameInfoTracker(
                                            new ScheduleTask(
                                                    new WindowTask(
                                                            new GameTask(
                                                                    new UiLayersTask(null, uiLayers, spriteBatch),
                                                                    game),
                                                            window),
                                                    schedule),
                                            appContext.frameInfo()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
            throw new RuntimeException(e);
        }
    }

    private AppWindow createWindow(MonitorInfo monitorInfo, UiLayers uiLayers) {
        return new WindowBuilder()
                .fullScreen(arguments.fullScreen())
                .version(3, 3)
                .coreProfile()
                .debug(arguments.debug())
                .monitor(monitorInfo.monitor())
                .dimensions(800, 600)
                .events(uiLayers.events())
                .build("LWJGL Playground");
    }

    private void runMainLoop(AppContext context, FrameTask task) throws Exception {
        final AtomicBoolean exitFlag = new AtomicBoolean();
        final AppWindow window = (AppWindow) context.window();
        try (var ac = context.commands().add("quit", () -> {
            logger.info("Exiting app...");
            exitFlag.set(true);
        })) {
            logger.info("Entering main loop...");
            // todo - remove that (why?)
            context.commands().execute("new-game");
            //
            while (!window.shouldClose() && !exitFlag.get()) {
                task.run();
            }
            context.configuration().persist();
        }
    }
}
