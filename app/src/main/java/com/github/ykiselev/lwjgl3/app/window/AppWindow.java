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

package com.github.ykiselev.lwjgl3.app.window;

import com.github.ykiselev.window.WindowEvents;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * Window class.
 * Creates window and sets keyboard, mouse and frame buffer resize callbacks
 */
public final class AppWindow implements AutoCloseable {

    private final long window;

    /**
     * Force frame buffer resize on window creation
     */
    private boolean frameBufferResized = true;

    private boolean windowResized;

    private final WindowEvents windowEvents;

    public AppWindow(long window, WindowEvents windowEvents) {
        this.window = window;
        this.windowEvents = requireNonNull(windowEvents);
    }

    /**
     * Event handlers left package-private to be accessible form {@link WindowBuilder} class.
     */
    void onFrameBufferSize(long window, int width, int height) {
        frameBufferResized = true;
    }

    /**
     * Event handlers left package-private to be accessible form {@link WindowBuilder} class.
     */
    void onWindowSize(long window, int width, int height) {
        windowResized = true;
    }

    /**
     * Event handlers left package-private to be accessible form {@link WindowBuilder} class.
     */
    void onKey(long window, int key, int scanCode, int action, int mods) {
        windowEvents.keyEvent(key, scanCode, action, mods);
    }

    /**
     * Event handlers left package-private to be accessible form {@link WindowBuilder} class.
     */
    void onCursorPosition(long window, double x, double y) {
        windowEvents.cursorEvent(x, y);
    }

    /**
     * Event handlers left package-private to be accessible form {@link WindowBuilder} class.
     */
    void onMouseButton(long window, int button, int action, int mods) {
        windowEvents.mouseButtonEvent(button, action, mods);
    }

    /**
     * Event handlers left package-private to be accessible form {@link WindowBuilder} class.
     */
    void onScroll(long window, double dx, double dy) {
        windowEvents.scrollEvent(dx, dy);
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
