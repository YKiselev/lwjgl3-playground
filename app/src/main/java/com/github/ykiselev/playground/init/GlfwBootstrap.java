package com.github.ykiselev.playground.init;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public final class GlfwBootstrap implements AutoCloseable {

    public GlfwBootstrap() {
        glfwInit();
    }

    @Override
    public void close() {
        glfwTerminate();
    }
}
