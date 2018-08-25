package com.github.ykiselev.lwjgl3.layers.def;

import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.lwjgl3.window.WindowEvents;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DefaultUiLayer implements UiLayer {

    private final Services services;

    private WindowEvents events = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            if (action == GLFW_PRESS) {
                services.resolve(Events.class)
                        .fire(new NewGameEvent());
            }
            return true;
        }

        @Override
        public void cursorEvent(double x, double y) {
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            return true;
        }

        @Override
        public void frameBufferResized(int width, int height) {
        }

        @Override
        public boolean scrollEvent(double dx, double dy) {
            return true;
        }
    };

    public DefaultUiLayer(Services services) {
        this.services = requireNonNull(services);
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    @Override
    public void draw(int width, int height) {
        // no-op
    }
}
