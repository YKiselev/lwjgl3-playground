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
import cob.github.ykiselev.lwjgl3.events.Subscriptions;
import cob.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import cob.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import cob.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.fs.AppFileSystem;
import cob.github.ykiselev.lwjgl3.host.AppHost;
import cob.github.ykiselev.lwjgl3.host.Host;
import cob.github.ykiselev.lwjgl3.host.OnNewGameEvent;
import cob.github.ykiselev.lwjgl3.host.OnShowMenuEvent;
import cob.github.ykiselev.lwjgl3.host.ProgramArguments;
import cob.github.ykiselev.lwjgl3.layers.AppUiLayers;
import cob.github.ykiselev.lwjgl3.layers.UiLayers;
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
            final GameAssets assets = new GameAssets(args.assetPaths());
            final AppUiLayers layers = new AppUiLayers();
            try (AppHost host = new AppHost()) {
                createServices(host, assets, layers);
                try (Subscriptions group = subscribe(host)) {
                    try (AppWindow window = new AppWindow(args.fullScreen())) {
                        GL.createCapabilities();
                        window.wireEvents(layers);
                        window.show();
                        host.events().send(new NewGameEvent());
                        glfwSwapInterval(1);
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

    private void createServices(AppHost host, Assets assets, UiLayers layers) throws IOException {
        logger.info("Creating services...");
        final Services services = host.services();
        services.add(Assets.class, assets);
        services.add(UiLayers.class, layers);
        services.add(FileSystem.class, new AppFileSystem(args.home()));
        services.add(PersistedConfiguration.class, new AppConfig(host));
        services.add(SoundEffects.class, new AppSoundEffects(host));
    }

    private Subscriptions subscribe(Host host) {
        return new SubscriptionsBuilder()
                .add(QuitGameEvent.class, this::onQuitGame)
                .add(NewGameEvent.class, new OnNewGameEvent(host))
                .add(ShowMenuEvent.class, new OnShowMenuEvent(host))
                .build(host.events());
    }

    private void onQuitGame(QuitGameEvent event) {
        exitFlag = true;
    }
}
