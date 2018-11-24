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

package com.github.ykiselev.opengl.sandbox;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * Experimenting with {@link GL11#GL_UNPACK_ALIGNMENT}.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class SandboxApp {

    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private long window;

    public long window() {
        return window;
    }

    protected void setWindowHints() {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }

    protected void init(String title, int width, int height) {
        glfwInit();
        glfwSetErrorCallback(errorCallback);
        setWindowHints();
        window = glfwCreateWindow(
                100, 100, title, 0, 0
        );
        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }
        glfwSetWindowSizeCallback(window, this::windowSizeChanged);
        glfwSetFramebufferSizeCallback(window, this::frameBufferSizeChanged);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(1);
        glfwSetWindowSize(window, width, height);
    }

    protected void frameBufferSizeChanged(long window, int width, int height) {
        // override if needed
    }

    protected void windowSizeChanged(long window, int width, int height) {
        // override if needed
    }

    protected void show() {
        glfwShowWindow(window);
    }

    protected abstract void update();

    protected void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwMakeContextCurrent(window);
            glfwPollEvents();
            update();
            glfwSwapBuffers(window);
        }
    }

    protected void destroy() {
        glfwTerminate();
        glfwSetErrorCallback(null);
        errorCallback.free();
    }

}
