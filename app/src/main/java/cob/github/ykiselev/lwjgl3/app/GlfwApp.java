package cob.github.ykiselev.lwjgl3.app;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GlfwApp implements Runnable {

    private final Runnable delegate;

    public GlfwApp(Runnable delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public void run() {
        glfwInit();
        try {
            delegate.run();
        } finally {
            glfwTerminate();
        }
    }
}
