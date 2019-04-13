/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.common.trigger;

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
