package cob.github.ykiselev.lwjgl3;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * Window class.
 * Note: Callback references are used from JNI and MUST be stored in fields to avoid garbage collection!
 * <p>
 * Creates window and sets keyboard, mouse and frame buffer resize callbacks
 */
final class AppWindow implements AutoCloseable {

    private static final String TITLE = "LWJGL3 Test App";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final long window;

    private volatile boolean frameBufferResized;

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWFramebufferSizeCallbackI frameBufferSizeCallback = new GLFWFramebufferSizeCallbackI() {
        @Override
        public void invoke(long window, int width, int height) {
            frameBufferResized = true;
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWKeyCallbackI keyCallback = (window, key, scancode, action, mods) ->
            logger.info("keyCallback({},{},{},{})", key, scancode, action, mods);

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWCursorPosCallbackI cursorPosCallback = (window, xpos, ypos) -> {
        //logger.info("cursorPos({},{})", xpos, ypos);
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final GLFWMouseButtonCallbackI mouseButtonCallback = (window, button, action, mods) ->
            logger.info("mouseButton({},{},{})", button, action, mods);

    AppWindow() {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        this.window = glfwCreateWindow(
                320, 240, TITLE, 0, 0
        );
        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }
        glfwMakeContextCurrent(window);
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCallback);
        glfwSetKeyCallback(window, keyCallback);
        glfwSetCursorPosCallback(window, cursorPosCallback);
        glfwSetMouseButtonCallback(window, mouseButtonCallback);
        glfwSetWindowSize(window, 800, 600);
    }

    @Override
    public void close() throws Exception {
        glfwDestroyWindow(window);
    }

    public void show() {
        glfwShowWindow(window);
    }

    public void hide() {
        glfwHideWindow(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void update() {
        glfwMakeContextCurrent(window);
        checkForFrameBufferResize();
        glfwPollEvents();
        glfwSwapBuffers(window);
    }

    private void checkForFrameBufferResize() {
        if (frameBufferResized) {
            final int[] w = new int[1], h = new int[1];
            glfwGetFramebufferSize(window, w, h);
            final int width = w[0];
            final int height = h[0];
            logger.info("frameBufferSize now is {}x{}", width, height);
            // todo - do something
            frameBufferResized = false;
        }
        BufferUtils.createByteBuffer(100);
        MemoryUtil.memAlloc(200);
    }
}
