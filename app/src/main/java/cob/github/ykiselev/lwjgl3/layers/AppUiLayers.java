package cob.github.ykiselev.lwjgl3.layers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppUiLayers implements UiLayers {

    private final Deque<UiLayer> layers = new ArrayDeque<>();

    private int width;

    private int height;

    @Override
    public void draw() {
        for (UiLayer layer : (Iterable<UiLayer>) layers::descendingIterator) {
            layer.draw(width, height);
        }
    }

    @Override
    public void replace(UiLayer layer) {
        layers.clear();
        layers.push(layer);
    }

    @Override
    public void push(UiLayer layer) {
        if (layers.contains(layer)) {
            throw new IllegalArgumentException("Already pushed:" + layer);
        }
        layers.push(layer);
    }

    @Override
    public void pop(UiLayer layer) {
        final UiLayer pop = layers.pop();
        if (pop != layer) {
            throw new IllegalArgumentException("Not a top layer: " + layer);
        }
    }

    private boolean dispatch(Predicate<UiLayer> p) {
        for (UiLayer layer : layers) {
            if (p.test(layer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        return dispatch(layer -> layer.keyEvent(key, scanCode, action, mods));
    }

    /**
     * Note: {@link com.github.ykiselev.opengl.sprites.SpriteBatch} uses left lower corner as an coordinate system origin (0,0)
     * while GLFW passes cursor coordinates relative to upper-left corner of the window, so here we translate {@code y} coordinate
     * to be 0 at the bottom of a window and {@code height} at the top.
     *
     * @param x the x coordinate (relative to window left)
     * @param y the y coordinate (relative to window top)
     */
    @Override
    public void cursorEvent(double x, double y) {
        dispatch(layer -> {
            layer.cursorEvent(x, height - y);
            return false;
        });
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        return dispatch(layer -> layer.mouseButtonEvent(button, action, mods));
    }

    @Override
    public void frameBufferResized(int width, int height) {
        this.width = width;
        this.height = height;
        layers.forEach(layer -> layer.frameBufferResized(width, height));
    }
}
