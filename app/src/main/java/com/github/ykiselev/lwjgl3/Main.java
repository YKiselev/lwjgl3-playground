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

import com.github.ykiselev.lwjgl3.host.ProgramArguments;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Main {

    public static void main(String[] args) {
        final ProgramArguments programArguments = new ProgramArguments(args);
        withExceptionCatching(
                () -> withErrorCallback(
                        () -> withGlfw(
                                new Host(
                                        programArguments,
                                        services -> new MainLoop(
                                                programArguments, services
                                        )
                                )
                        )
                )
        );
    }

    private static void withErrorCallback(Runnable delegate) {
        try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
            final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
            try {
                delegate.run();
            } finally {
                glfwSetErrorCallback(previous);
            }
        }
    }

    private static void withGlfw(Runnable delegate) {
        glfwInit();
        try {
            delegate.run();
        } finally {
            glfwTerminate();
        }
    }

    private static void withExceptionCatching(Runnable delegate) {
        try {
            delegate.run();
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error("Unhandled exception!", e);
        }
    }

}
