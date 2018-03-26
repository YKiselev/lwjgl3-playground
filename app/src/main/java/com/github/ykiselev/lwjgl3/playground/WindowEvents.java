package com.github.ykiselev.lwjgl3.playground;

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
    boolean keyEvent(int key, int scanCode, int action, int mods);

    /**
     * @see GLFWCursorPosCallbackI#invoke(long, double, double)
     */
    void cursorEvent(double x, double y);

    /**
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWMouseButtonCallbackI#invoke(long, int, int, int)
     */
    boolean mouseButtonEvent(int button, int action, int mods);

    /**
     * @param width  the new width of frame buffer
     * @param height the new height of frame buffer
     * @see GLFWFramebufferSizeCallbackI#invoke(long, int, int)
     */
    void frameBufferResized(int width, int height);

    /**
     * @see GLFWScrollCallback#invoke(long, double, double)
     */
    boolean scrollEvent(double dx, double dy);
}
