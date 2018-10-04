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

package com.github.ykiselev.playground.app.window;

import com.github.ykiselev.window.WindowEvents;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_ANY_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_COMPAT_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WindowBuilder {

    private boolean fullScreen;

    private long monitor;

    private int width;

    private int height;

    private WindowEvents events = new WindowEvents() {
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

    public WindowBuilder windowed() {
        fullScreen = false;
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        return this;
    }

    public WindowBuilder fullsScreen() {
        fullScreen = true;
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        return this;
    }

    public WindowBuilder fullScreen(boolean fullScreen) {
        return fullScreen ? fullsScreen() : windowed();
    }

    public WindowBuilder version(int major, int minor) {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, major);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, minor);
        return this;
    }

    public WindowBuilder anyProfile() {
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        return this;
    }

    public WindowBuilder coreProfile() {
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        return this;
    }

    public WindowBuilder compatibleProfile() {
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        return this;
    }

    public WindowBuilder debug(boolean value) {
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, value ? GL_TRUE : GL_FALSE);
        return this;
    }

    public WindowBuilder monitor(long monitor) {
        this.monitor = monitor;
        return this;
    }

    public WindowBuilder primaryMonitor() {
        return monitor(glfwGetPrimaryMonitor());
    }

    public WindowBuilder dimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public WindowBuilder events(WindowEvents events) {
        this.events = requireNonNull(events);
        return this;
    }

    public AppWindow build(String title) {
        final long window = glfwCreateWindow(
                width,
                height,
                title,
                fullScreen ? monitor : 0L,
                0
        );
        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        final Callback debugMessageCallback;
        if (glfwGetWindowAttrib(window, GLFW_OPENGL_DEBUG_CONTEXT) == GLFW_TRUE) {
            debugMessageCallback = GLUtil.setupDebugMessageCallback();
        } else {
            debugMessageCallback = null;
        }
        final AppWindow result = new AppWindow(window, events, debugMessageCallback);
        glfwSetFramebufferSizeCallback(window, GLFWFramebufferSizeCallback.create(result::onFrameBufferSize));
        glfwSetWindowSizeCallback(window, GLFWWindowSizeCallback.create(result::onWindowSize));
        glfwSetKeyCallback(window, GLFWKeyCallback.create(result::onKey));
        glfwSetCursorPosCallback(window, GLFWCursorPosCallback.create(result::onCursorPosition));
        glfwSetScrollCallback(window, GLFWScrollCallback.create(result::onScroll));
        glfwSetMouseButtonCallback(window, GLFWMouseButtonCallback.create(result::onMouseButton));
        return result;
    }
}
