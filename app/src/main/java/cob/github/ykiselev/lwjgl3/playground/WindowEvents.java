package cob.github.ykiselev.lwjgl3.playground;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
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

}
