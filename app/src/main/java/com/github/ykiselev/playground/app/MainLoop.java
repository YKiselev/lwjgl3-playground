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

package com.github.ykiselev.playground.app;

import com.github.ykiselev.FrameInfo;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.common.ThrowingRunnable;
import com.github.ykiselev.playground.app.window.AppWindow;
import com.github.ykiselev.playground.app.window.WindowBuilder;
import com.github.ykiselev.playground.host.ConsoleFactory;
import com.github.ykiselev.playground.host.GameEvents;
import com.github.ykiselev.playground.host.MenuEvents;
import com.github.ykiselev.playground.host.ProgramArguments;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.commands.EventFiringHandler;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.game.NewGameEvent;
import com.github.ykiselev.services.events.game.QuitEvent;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.services.schedule.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MainLoop implements ThrowingRunnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProgramArguments args;

    private final Services services;

    private final FrameInfo frameInfo = new FrameInfo(60);

    private volatile boolean exitFlag;

    public MainLoop(ProgramArguments args, Services services) {
        this.args = requireNonNull(args);
        this.services = requireNonNull(services);
    }

    @Override
    public void run() throws Exception {
        try (AppWindow window = createWindow(services);
             AutoCloseable ac = subscribe();
             GameEvents gameEvents = new GameEvents(services)
        ) {
            window.show();
            // todo - remove that
            services.resolve(Events.class)
                    .fire(NewGameEvent.INSTANCE);
            //
            glfwSwapInterval(args.swapInterval());
            logger.info("Entering main loop...");
            final Schedule schedule = services.resolve(Schedule.class);
            final UiLayers layers = services.resolve(UiLayers.class);
            while (!window.shouldClose() && !exitFlag) {
                final double t0 = glfwGetTime();
                gameEvents.update();
                window.checkEvents();
                layers.draw();
                window.swapBuffers();
                schedule.processPendingTasks(2);
                final double t1 = glfwGetTime();
                frameInfo.add((t1 - t0) * 1000.0);
            }
        }
    }

    private AppWindow createWindow(Services services) {
        return new WindowBuilder()
                .fullScreen(args.fullScreen())
                .version(3, 3)
                .coreProfile()
                .debug(true)
                .primaryMonitor()
                .dimensions(800, 600)
                .events(services.resolve(UiLayers.class).events())
                .build("LWJGL Playground");
    }

    private CompositeAutoCloseable subscribe() {
        return new CompositeAutoCloseable(
                services.resolve(Events.class)
                        .subscribe(QuitEvent.class, this::onQuit),
                services.resolve(Commands.class)
                        .add("quit", new EventFiringHandler<>(services, QuitEvent.INSTANCE)),
                new ConsoleFactory(services)
                        .create(),
                services.add(FrameInfo.class, frameInfo)
        ).with(new MenuEvents(services));
    }

    private void onQuit() {
        exitFlag = true;
    }
}
