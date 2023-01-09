package com.github.ykiselev.playground.init;

import com.github.ykiselev.common.closeables.Closeables;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

public final class ErrorCallbackBootstrap implements AutoCloseable {

    private final GLFWErrorCallback callback;

    private final GLFWErrorCallback previous;

    public ErrorCallbackBootstrap() {
        this.callback = GLFWErrorCallback.createPrint(System.err);
        this.previous = glfwSetErrorCallback(callback);
    }

    @Override
    public void close() {
        glfwSetErrorCallback(previous);
        Closeables.close(callback);
    }
}
