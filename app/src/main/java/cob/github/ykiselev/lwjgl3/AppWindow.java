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

import cob.github.ykiselev.lwjgl3.playground.WindowCallbacks;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * Window class.
 * Note: Callback references are used from JNI and MUST be stored in fields to avoid garbage collection!
 * <p>
 * Creates window and sets keyboard, mouse and frame buffer resize callbacks
 */
final class AppWindow implements AutoCloseable {

    private static final String TITLE = "LWJGL3 Test App";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WindowCallbacks callbacks;

    private final long window;

    private volatile boolean frameBufferResized;

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWFramebufferSizeCallbackI frameBufferSizeCallback = new GLFWFramebufferSizeCallbackI() {
        @Override
        public void invoke(long window, int width, int height) {
            frameBufferResized = true;
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWKeyCallbackI keyCallback;

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWCursorPosCallbackI cursorPosCallback;

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWMouseButtonCallbackI mouseButtonCallback;

    AppWindow(WindowCallbacks callbacks) {
        this.callbacks = requireNonNull(callbacks);
        this.keyCallback = (window, key, scancode, action, mods) -> callbacks.keyEvent(key, scancode, action, mods);
        this.cursorPosCallback = (window, xpos, ypos) -> callbacks.cursorEvent(xpos, ypos);
        this.mouseButtonCallback = (window, button, action, mods) -> callbacks.mouseButtonEvent(button, action, mods);

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // todo ?
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);
        this.window = glfwCreateWindow(
                100, 100, TITLE, 0, 0
        );
        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }
        glfwMakeContextCurrent(window);
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCallback);
        glfwSetKeyCallback(window, keyCallback);
        glfwSetCursorPosCallback(window, cursorPosCallback);
        glfwSetMouseButtonCallback(window, mouseButtonCallback);
        glfwSetWindowSize(window, 800, 600);
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
        glfwPollEvents();
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
            callbacks.frameBufferEvent(width, height);
        }
    }
}
