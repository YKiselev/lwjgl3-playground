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

import cob.github.ykiselev.lwjgl3.assets.GameAssets;
import cob.github.ykiselev.lwjgl3.layers.AppUiLayers;
import cob.github.ykiselev.lwjgl3.layers.Menu;
import cob.github.ykiselev.lwjgl3.playground.Game;
import cob.github.ykiselev.lwjgl3.window.AppWindow;
import com.github.ykiselev.assets.Assets;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private final ProgramArguments args;

    private App(ProgramArguments args) {
        this.args = args;
    }

    public static void main(String[] args) {
        new App(new ProgramArguments(args)).run();
    }

    private void run() {
        glfwInit();
        try {
            glfwSetErrorCallback(errorCallback);
            try (GameAssets assets = new GameAssets(args.assetPaths())) {
                try (AppWindow window = new AppWindow(args.fullScreen())) {
                    GL.createCapabilities();
                    try (Game game = newGame(assets)) {
                        final AppUiLayers uiLayers = new AppUiLayers(new Menu(assets), game);
                        window.wireWindowEvents(uiLayers);
                        window.wireFrameBufferEvents(uiLayers);
                        window.show();
                        uiLayers.push(game);
                        while (!window.shouldClose()) {
                            window.checkEvents();
                            game.update();
                            uiLayers.draw();
                            window.swapBuffers();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unhandled exception!", e);
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null);
            errorCallback.free();
        }
    }

    private Game newGame(Assets assets) {
        return new Game(assets);
    }
}
