package com.github.ykiselev.lwjgl3.playground;

import static java.util.Objects.requireNonNull;

/**
 * This class can be used as a boilerplate in cases when you only need to override some of {@link WindowEvents} methods.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class DelegatingWindowEvents implements WindowEvents {

    private final WindowEvents delegate;

    public DelegatingWindowEvents(WindowEvents delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        return delegate.keyEvent(key, scanCode, action, mods);
    }

    @Override
    public void cursorEvent(double x, double y) {
        delegate.cursorEvent(x, y);
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        return delegate.mouseButtonEvent(button, action, mods);
    }

    @Override
    public void frameBufferResized(int width, int height) {
        delegate.frameBufferResized(width, height);
    }

    @Override
    public boolean scrollEvent(double dx, double dy) {
        return delegate.scrollEvent(dx, dy);
    }
}
