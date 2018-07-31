package com.github.ykiselev.lwjgl3.app;

import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GlfwApp implements Callable<Void> {

    private final Callable<Void> delegate;

    public GlfwApp(Callable<Void> delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public Void call() throws Exception {
        glfwInit();
        try {
            return delegate.call();
        } finally {
            glfwTerminate();
        }
    }
}
