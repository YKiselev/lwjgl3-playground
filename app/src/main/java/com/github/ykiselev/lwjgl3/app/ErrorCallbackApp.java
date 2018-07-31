package com.github.ykiselev.lwjgl3.app;

import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ErrorCallbackApp implements Callable<Void> {

    private final Callable<Void> delegate;

    public ErrorCallbackApp(Callable<Void> delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public Void call() throws Exception {
        final GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err);
        final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
        try {
            return delegate.call();
        } finally {
            glfwSetErrorCallback(previous);
            callback.free();
        }
    }
}
