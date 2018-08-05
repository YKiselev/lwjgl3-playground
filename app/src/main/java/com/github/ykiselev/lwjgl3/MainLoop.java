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

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import com.github.ykiselev.lwjgl3.host.ProgramArguments;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.lwjgl3.services.schedule.Schedule;
import com.github.ykiselev.lwjgl3.window.AppWindow;
import com.github.ykiselev.lwjgl3.window.WindowBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MainLoop implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProgramArguments args;

    private final Services services;

    private volatile boolean exitFlag;

    public MainLoop(ProgramArguments args, Services services) {
        this.args = requireNonNull(args);
        this.services = requireNonNull(services);
    }

    @Override
    public void run() {
        try (AppWindow window = createWindow(services);
             CompositeAutoCloseable ac = subscribe()
        ) {
            window.show();
            services.resolve(Events.class)
                    .fire(new NewGameEvent());
            glfwSwapInterval(args.swapInterval());
            logger.info("Entering main loop...");
            final Schedule schedule = services.resolve(Schedule.class);
            final UiLayers layers = services.resolve(UiLayers.class);
            while (!window.shouldClose() && !exitFlag) {
                window.checkEvents();
                layers.draw();
                window.swapBuffers();
                schedule.processPendingTasks(2);
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
        return new SubscriptionsBuilder(services.resolve(Events.class))
                .with(QuitGameEvent.class, this::onQuitGame)
                .build();
    }

    private QuitGameEvent onQuitGame(QuitGameEvent event) {
        exitFlag = true;
        return null;
    }
}
