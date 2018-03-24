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

package cob.github.ykiselev.lwjgl3;

import cob.github.ykiselev.lwjgl3.app.ErrorCallbackApp;
import cob.github.ykiselev.lwjgl3.app.GlfwApp;
import cob.github.ykiselev.lwjgl3.assets.GameAssets;
import cob.github.ykiselev.lwjgl3.config.AppConfig;
import cob.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import cob.github.ykiselev.lwjgl3.events.AppEvents;
import cob.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import cob.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import cob.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import cob.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.fs.AppFileSystem;
import cob.github.ykiselev.lwjgl3.fs.ClassPathResources;
import cob.github.ykiselev.lwjgl3.fs.DiskResources;
import cob.github.ykiselev.lwjgl3.host.OnNewGameEvent;
import cob.github.ykiselev.lwjgl3.host.OnShowMenuEvent;
import cob.github.ykiselev.lwjgl3.host.ProgramArguments;
import cob.github.ykiselev.lwjgl3.layers.AppUiLayers;
import cob.github.ykiselev.lwjgl3.layers.UiLayers;
import cob.github.ykiselev.lwjgl3.services.MapBasedServices;
import cob.github.ykiselev.lwjgl3.services.Services;
import cob.github.ykiselev.lwjgl3.services.SoundEffects;
import cob.github.ykiselev.lwjgl3.sound.AppSoundEffects;
import cob.github.ykiselev.lwjgl3.window.AppWindow;
import com.github.ykiselev.assets.Assets;
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
                        while (!window.shouldClose() && !exitFlag) {
                            window.checkEvents();
                            layers.draw();
                            window.swapBuffers();
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
