package cob.github.ykiselev.lwjgl3;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    public static void main(String[] args) throws Exception {
        new App().run();
    }

    private void run() throws Exception {
        glfwInit();
        try {
            glfwSetErrorCallback(errorCallback);
            try (AppWindow window = new AppWindow()) {
                GL.createCapabilities();
                window.show();
                while (!window.shouldClose()) {
                    window.update();
                    Thread.sleep(10);
                }
            }
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null);
            errorCallback.free();
        }
    }
}
