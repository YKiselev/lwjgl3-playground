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

package cob.github.ykiselev.lwjgl3.window;

import cob.github.ykiselev.lwjgl3.playground.WindowEvents;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.util.Objects.requireNonNull;
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
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWFramebufferSizeCallbackI frameBufferSizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            frameBufferResized = true;
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWWindowSizeCallbackI windowSizeCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            windowResized = true;
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWKeyCallbackI keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            windowEvents.keyEvent(key, scancode, action, mods);
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWCursorPosCallbackI cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            windowEvents.cursorEvent(xpos, ypos);
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWMouseButtonCallbackI mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            windowEvents.mouseButtonEvent(button, action, mods);
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
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCallback);
        glfwSetWindowSizeCallback(window, windowSizeCallback);
        glfwSetKeyCallback(window, keyCallback);
        glfwSetCursorPosCallback(window, cursorPosCallback);
        glfwSetMouseButtonCallback(window, mouseButtonCallback);
        windowResized = true;
        frameBufferResized = true;
    }

    public void wireWindowEvents(WindowEvents events) {
        this.windowEvents = requireNonNull(events);
    }

    @Override
    public void close() {
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
