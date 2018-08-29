package com.github.ykiselev.lwjgl3.layers;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.window.WindowEvents;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppUiLayers implements UiLayers {

    private final Deque<UiLayer> layers = new ArrayDeque<>();

    private final WindowEvents events = new WindowEvents() {

        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            return dispatch(events -> events.keyEvent(key, scanCode, action, mods));
        }

        /**
         * Note: {@link SpriteBatch} uses left lower corner as an coordinate system origin (0,0)
         * while GLFW passes cursor coordinates relative to upper-left corner of the window, so here we translate {@code y} coordinate
         * to be 0 at the bottom of a window and {@code height} at the top.
         *
         * @param x the x coordinate (relative to window left)
         * @param y the y coordinate (relative to window top)
         */
        @Override
        public void cursorEvent(double x, double y) {
            dispatch(events -> {
                events.cursorEvent(x, height - y);
                return false;
            });
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            return dispatch(layer -> layer.mouseButtonEvent(button, action, mods));
        }

        @Override
        public void frameBufferResized(int width, int height) {
            resize(width, height);
            layers.forEach(layer -> layer.events().frameBufferResized(width, height));
        }

        @Override
        public boolean scrollEvent(double dx, double dy) {
            return dispatch(layer -> layer.scrollEvent(dx, dy));
        }

    };

    private int width;

    private int height;

    private void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    @Override
    public void draw() {
        for (UiLayer layer : (Iterable<UiLayer>) layers::descendingIterator) {
            layer.draw(width, height);
        }
    }

    @Override
    public void bringToFront(UiLayer layer) {
        if (layer == null) {
            throw new NullPointerException("Layer can not be null!");
        }
        while (!layers.isEmpty()) {
            if (!layers.peek().isPopup()) {
                break;
            }
            layers.pop().onPop();
        }
        remove(layer);
        push(layer);
    }

    @Override
    public void push(UiLayer layer) {
        if (layer == null) {
            throw new NullPointerException("Layer can not be null!");
        }
        if (layers.contains(layer)) {
            throw new IllegalArgumentException("Already pushed:" + layer);
        }
        layer.onPush();
        layers.push(layer);
    }

    @Override
    public void pop(UiLayer layer) {
        if (layer == null) {
            throw new NullPointerException("Layer can not be null!");
        }
        final UiLayer pop = layers.peek();
        if (pop != layer) {
            throw new IllegalArgumentException("Not a top layer: " + layer);
        }
        layers.pop().onPop();
    }

    @Override
    public void remove(UiLayer layer) {
        if (layers.remove(layer)) {
            layer.onPop();
        }
    }

    private boolean dispatch(Predicate<WindowEvents> p) {
        for (UiLayer layer : layers) {
            if (p.test(layer.events())) {
                return true;
            }
        }
        return false;
    }

}
