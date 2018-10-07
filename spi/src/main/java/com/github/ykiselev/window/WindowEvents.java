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

package com.github.ykiselev.window;

import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallback;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface WindowEvents {

    /**
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    default boolean keyEvent(int key, int scanCode, int action, int mods) {
        return false;
    }

    /**
     * @param codePoint the Unicode code point of the character
     * @see GLFWCharCallbackI#invoke(long, int)
     */
    default boolean charEvent(int codePoint) {
        return false;
    }

    /**
     * @see GLFWCursorPosCallbackI#invoke(long, double, double)
     */
    default void cursorEvent(double x, double y) {

    }

    /**
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWMouseButtonCallbackI#invoke(long, int, int, int)
     */
    default boolean mouseButtonEvent(int button, int action, int mods) {
        return false;
    }

    /**
     * @param width  the new width of frame buffer
     * @param height the new height of frame buffer
     * @see GLFWFramebufferSizeCallbackI#invoke(long, int, int)
     */
    default void frameBufferResized(int width, int height) {

    }

    /**
     * @see GLFWScrollCallback#invoke(long, double, double)
     */
    default boolean scrollEvent(double dx, double dy) {
        return false;
    }


}
