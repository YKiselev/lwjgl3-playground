package com.github.ykiselev.lwjgl3.events.config;

import com.github.ykiselev.lwjgl3.config.PersistedConfiguration;

import static java.util.Objects.requireNonNull;

/**
 * Event is fired by {@link PersistedConfiguration#set(String, Object)}.
 * Subscribers can review new value and prevent it from being applied by returning new event or {@code null}.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ValueChangingEvent {

    /**
     * Configuration element path, for example "sound.effects.volume"
     */
    private final String path;

    /**
     * Current value of configuration element
     */
    private final Object oldValue;

    /**
     * New value of configuration element
     */
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

    public ValueChangingEvent with(Object value) {
        return new ValueChangingEvent(path, oldValue, value);
    }

}
