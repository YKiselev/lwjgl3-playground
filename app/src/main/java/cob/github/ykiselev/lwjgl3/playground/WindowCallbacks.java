package cob.github.ykiselev.lwjgl3.playground;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface WindowCallbacks {

    /**
     * @see GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    void keyEvent(int key, int scanCode, int action, int mods);

    /**
     * @see GLFWCursorPosCallbackI#invoke(long, double, double)
     */
    void cursorEvent(double x, double y);

    /**
     * @see GLFWMouseButtonCallbackI#invoke(long, int, int, int)
     */
    void mouseButtonEvent(int button, int action, int mods);

    /**
     * @param width  the new width of frame buffer
     * @param height the new height of frame buffer
     * @see GLFWFramebufferSizeCallbackI#invoke(long, int, int)
     */
    void frameBufferEvent(int width, int height);

    final class NoOp implements WindowCallbacks {

        @Override
        public void keyEvent(int key, int scanCode, int action, int mods) {
        }

        @Override
        public void cursorEvent(double x, double y) {
        }

        @Override
        public void mouseButtonEvent(int button, int action, int mods) {
        }

        @Override
        public void frameBufferEvent(int width, int height) {
        }
    }
}
