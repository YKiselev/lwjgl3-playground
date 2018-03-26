package com.github.ykiselev.lwjgl3.app;

import org.lwjgl.glfw.GLFWErrorCallback;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ErrorCallbackApp implements Runnable {

    private final Runnable delegate;

    public ErrorCallbackApp(Runnable delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public void run() {
        final GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err);
        final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
        try {
            delegate.run();
        } finally {
            glfwSetErrorCallback(previous);
            callback.free();
        }
    }
}
