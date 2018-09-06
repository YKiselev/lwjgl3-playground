package com.github.ykiselev.trigger;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Trigger {

    enum State {
        NONE, ON, OFF
    }

    private volatile State state = State.NONE;

    private final Runnable whenOn;

    private final Runnable whenOff;

    public Trigger(Runnable whenOn, Runnable whenOff) {
        this.whenOn = whenOn;
        this.whenOff = whenOff;
    }

    private State state(boolean value) {
        return value ? State.ON : State.OFF;
    }

    public void value(boolean value) {
        final State newState = state(value);
        if (state != newState) {
            state = newState;
            if (value) {
                if (whenOn != null) {
                    whenOn.run();
                }
            } else if (whenOff != null) {
                whenOff.run();
            }
        }
    }
}
