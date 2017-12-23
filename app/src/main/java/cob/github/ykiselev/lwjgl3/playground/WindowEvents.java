package cob.github.ykiselev.lwjgl3.playground;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

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
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWCursorPosCallbackI#invoke(long, double, double)
     */
    boolean cursorEvent(double x, double y);

    /**
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWMouseButtonCallbackI#invoke(long, int, int, int)
     */
    boolean mouseButtonEvent(int button, int action, int mods);

    /**
     * @param width  the new width of frame buffer
     * @param height the new height of frame buffer
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWFramebufferSizeCallbackI#invoke(long, int, int)
     */
    boolean frameBufferEvent(int width, int height);

    final class NoOp implements WindowEvents {

        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            return false;
        }

        @Override
        public boolean cursorEvent(double x, double y) {
            return false;
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            return false;
        }

        @Override
        public boolean frameBufferEvent(int width, int height) {
            return false;
        }
    }
}
