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

package com.github.ykiselev.trigger;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class TriggerTest {

    private final AtomicInteger flipped = new AtomicInteger();

    private final AtomicInteger flopped = new AtomicInteger();

    private final Trigger trigger = new Trigger(
            flipped::incrementAndGet,
            flopped::incrementAndGet
    );

    @Test
    public void shouldSwitchOn() {
        trigger.value(true);
        trigger.value(true);
        assertEquals(1, flipped.get());
        assertEquals(0, flopped.get());
    }

    @Test
    public void shouldSwitchOff() {
        trigger.value(false);
        trigger.value(false);
        assertEquals(0, flipped.get());
        assertEquals(1, flopped.get());
    }

    @Test
    public void shouldSwitchOnOff() {
        trigger.value(true);
        trigger.value(false);
        trigger.value(true);
        trigger.value(false);
        assertEquals(2, flipped.get());
        assertEquals(2, flopped.get());
    }

}