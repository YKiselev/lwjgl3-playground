package com.github.ykiselev.playground.ui;

/**
 * Base class for UI element.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class AbstractUiElement implements UiElement {

    private boolean enabled = true;

    private boolean visible = true;

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (isEventTarget()) {
            return onKey(key, scanCode, action, mods);
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void visible(boolean value) {
        if (visible != value) {
            visible = value;
            onVisible();
        }
    }

    @Override
    public void cursorEvent(double x, double y) {
        if (isEventTarget()) {
            onCursor(x, y);
        }
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (isEventTarget()) {
            return onMouseButton(button, action, mods);
        }
        return false;
    }

    @Override
    public boolean scrollEvent(double dx, double dy) {
        if (isEventTarget()) {
            return onScroll(dx, dy);
        }
        return false;
    }

    @Override
    public void frameBufferResized(int width, int height) {
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable(boolean value) {
        if (enabled != value) {
            enabled = value;
            onEnable();
        }
    }

    /**
     * @return {@code true} if UI element is visible and enabled
     */
    public boolean isEventTarget() {
        return isVisible() && isEnabled();
    }

    /**
     * Expected to be overridden
     */
    protected void onVisible() {
    }

    /**
     * Expected to be overridden
     */
    protected void onEnable() {
    }

    /**
     * Expected to be overridden
     */
    protected boolean onKey(int key, int scanCode, int action, int mods) {
        return false;
    }

    /**
     * Expected to be overridden
     */
    protected void onCursor(double x, double y) {
    }

    /**
     * Expected to be overridden
     */
    protected boolean onMouseButton(int button, int action, int mods) {
        return false;
    }

    /**
     * Expected to be overridden
     */
    protected boolean onScroll(double dx, double dy) {
        return false;
    }
}
