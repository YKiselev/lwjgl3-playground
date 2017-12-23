package cob.github.ykiselev.lwjgl3.playground;

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface FrameBufferEvents {

    /**
     * @param width  the new width of frame buffer
     * @param height the new height of frame buffer
     * @return {@code true} if event was handled or {@code false} to pass event to next handler in chain
     * @see GLFWFramebufferSizeCallbackI#invoke(long, int, int)
     */
    boolean frameBufferResized(int width, int height);
}
