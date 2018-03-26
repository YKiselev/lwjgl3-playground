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

package com.github.ykiselev.lwjgl3.window;

import com.github.ykiselev.lwjgl3.playground.WindowEvents;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * Window class.
 * Note: Callback references are used from JNI and MUST be stored in fields to avoid garbage collection!
 * <p>
 * Creates window and sets keyboard, mouse and frame buffer resize callbacks
 */
public final class AppWindow implements AutoCloseable {

    private static final String TITLE = "LWJGL3 Playground";

    private final long window;

    private boolean frameBufferResized;

    private boolean windowResized;

    private WindowEvents windowEvents = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            return true;
        }

        @Override
        public void cursorEvent(double x, double y) {
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            return true;
        }

        @Override
        public void frameBufferResized(int width, int height) {
        }

        @Override
        public boolean scrollEvent(double dx, double dy) {
            return true;
        }
    };

    public AppWindow(boolean fullScreen) {
        if (fullScreen) {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        } else {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // todo ?
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);

        final int width, height;
        final long monitor;
        if (fullScreen) {
            monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            width = mode.width();
            height = mode.height();
        } else {
            monitor = 0;
            width = 800;
            height = 600;
        }
        this.window = glfwCreateWindow(
                width, height, TITLE, monitor, 0
        );
        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }
        glfwMakeContextCurrent(window);
        glfwSetFramebufferSizeCallback(window, GLFWFramebufferSizeCallback.create(this::onFrameBufferSize));
        glfwSetWindowSizeCallback(window, GLFWWindowSizeCallback.create(this::onWindowSize));
        glfwSetKeyCallback(window, GLFWKeyCallback.create(this::onKey));
        glfwSetCursorPosCallback(window, GLFWCursorPosCallback.create(this::onCursorPosition));
        glfwSetScrollCallback(window, GLFWScrollCallback.create(this::onScroll));
        glfwSetMouseButtonCallback(window, GLFWMouseButtonCallback.create(this::onMouseButton));
        windowResized = true;
        frameBufferResized = true;
    }

    private void onFrameBufferSize(long window, int width, int height) {
        frameBufferResized = true;
    }

    private void onWindowSize(long window, int width, int height) {
        windowResized = true;
    }

    private void onKey(long window, int key, int scanCode, int action, int mods) {
        windowEvents.keyEvent(key, scanCode, action, mods);
    }

    private void onCursorPosition(long window, double x, double y) {
        windowEvents.cursorEvent(x, y);
    }

    private void onMouseButton(long window, int button, int action, int mods) {
        windowEvents.mouseButtonEvent(button, action, mods);
    }

    private void onScroll(long window, double dx, double dy) {
        windowEvents.scrollEvent(dx, dy);
    }

    public void wireEvents(WindowEvents events) {
        this.windowEvents = requireNonNull(events);
    }

    @Override
    public void close() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public void show() {
        glfwShowWindow(window);
    }

    public void hide() {
        glfwHideWindow(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void checkEvents() {
        glfwMakeContextCurrent(window);
        checkForFrameBufferResize();
        checkWindowResize();
        glfwPollEvents();
    }

    private void checkWindowResize() {
        if (windowResized) {
            windowResized = false;
            // todo - callback?
        }
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    private void checkForFrameBufferResize() {
        if (frameBufferResized) {
            final int width, height;
            try (MemoryStack ms = MemoryStack.stackPush()) {
                final IntBuffer wb = ms.callocInt(1);
                final IntBuffer hb = ms.callocInt(1);
                glfwGetFramebufferSize(window, wb, hb);
                width = wb.get(0);
                height = hb.get(0);
            }
            frameBufferResized = false;
            windowEvents.frameBufferResized(width, height);
        }
    }
}
