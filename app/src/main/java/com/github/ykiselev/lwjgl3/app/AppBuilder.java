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

package com.github.ykiselev.lwjgl3.app;

import com.github.ykiselev.lwjgl3.Main;
import org.apache.logging.log4j.io.IoBuilder;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppBuilder {

    private final Runnable delegate;

    public AppBuilder(Runnable delegate) {
        this.delegate = requireNonNull(delegate);
    }

    public AppBuilder withErrorCallback() {
        return new AppBuilder(() -> withErrorCallback(delegate));
    }

    public AppBuilder withGlfw() {
        return new AppBuilder(() -> withGlfw(delegate));
    }

    public AppBuilder withExceptionCatching() {
        return new AppBuilder(() -> withExceptionCatching(delegate));
    }

    public AppBuilder withLogging() {
        return new AppBuilder(() -> withLogging(delegate));
    }

    private void withErrorCallback(Runnable delegate) {
        try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
            final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
            try {
                delegate.run();
            } finally {
                glfwSetErrorCallback(previous);
            }
        }
    }

    private void withGlfw(Runnable delegate) {
        glfwInit();
        try {
            delegate.run();
        } finally {
            glfwTerminate();
        }
    }

    private void withExceptionCatching(Runnable delegate) {
        try {
            delegate.run();
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error("Unhandled exception!", e);
        }
    }

    private void withLogging(Runnable delegate) {
        System.setOut(IoBuilder.forLogger().buildPrintStream());
        System.setErr(IoBuilder.forLogger().buildPrintStream());
        delegate.run();
    }

    public void run() {
        delegate.run();
    }
}
