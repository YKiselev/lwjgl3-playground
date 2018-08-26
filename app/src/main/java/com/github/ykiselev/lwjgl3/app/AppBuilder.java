package com.github.ykiselev.lwjgl3.app;

import com.github.ykiselev.lwjgl3.Main;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppBuilder {

    private final Runnable delegate;

    public AppBuilder(Runnable delegate) {
        this.delegate = requireNonNull(delegate);
    }

    public AppBuilder withErrorCallback() {
        return new AppBuilder(() -> withErrorCallback(delegate));
    }

    public AppBuilder withGlfw() {
        return new AppBuilder(() -> withGlfw(delegate));
    }

    public AppBuilder withExceptionCatching() {
        return new AppBuilder(() -> withExceptionCatching(delegate));
    }

    public AppBuilder withLogging() {
        return new AppBuilder(() -> withLogging(delegate));
    }

    private void withErrorCallback(Runnable delegate) {
        try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
            final GLFWErrorCallback previous = glfwSetErrorCallback(callback);
            try {
                delegate.run();
            } finally {
                glfwSetErrorCallback(previous);
            }
        }
    }

    private void withGlfw(Runnable delegate) {
        glfwInit();
        try {
            delegate.run();
        } finally {
            glfwTerminate();
        }
    }

    private void withExceptionCatching(Runnable delegate) {
        try {
            delegate.run();
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error("Unhandled exception!", e);
        }
    }

    private void withLogging(Runnable delegate) {
        Configuration.DEBUG_STREAM.set(LwjglToLog4j2.class.getName());
        delegate.run();
    }

    public void run() {
        delegate.run();
    }
}
