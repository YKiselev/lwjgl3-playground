package com.github.ykiselev.lwjgl3.events.config;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ValueChangingEvent {

    private final String path;

    private final Object oldValue;

    private final Object newValue;

    public String path() {
        return path;
    }

    public Object oldValue() {
        return oldValue;
    }

    public Object newValue() {
        return newValue;
    }

    public ValueChangingEvent(String path, Object oldValue, Object newValue) {
        this.path = requireNonNull(path);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "ValueChangingEvent{" +
                "path='" + path + '\'' +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}
