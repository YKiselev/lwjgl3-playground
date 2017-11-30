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

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    public static void main(String[] args) throws Exception {
        new App().run();
    }

    private void run() throws Exception {
        glfwInit();
        try {
            glfwSetErrorCallback(errorCallback);
            try (AppWindow window = new AppWindow()) {
                GL.createCapabilities();
                window.show();
                while (!window.shouldClose()) {
                    window.update();
                    Thread.sleep(10);
                }
            }
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null);
            errorCallback.free();
        }
    }
}
