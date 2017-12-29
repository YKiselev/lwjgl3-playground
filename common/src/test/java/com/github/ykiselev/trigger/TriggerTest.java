package com.github.ykiselev.trigger;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class TriggerTest {

    private final AtomicInteger flipped = new AtomicInteger();

    private final AtomicInteger flopped = new AtomicInteger();

    private final Trigger trigger = new Trigger(
            flipped::incrementAndGet,
            flopped::incrementAndGet
    );

    @Test
    void shouldSwitchOn() {
        trigger.value(true);
        trigger.value(true);
        assertEquals(1, flipped.get());
        assertEquals(0, flopped.get());
    }

    @Test
    void shouldSwitchOff() {
        trigger.value(false);
        trigger.value(false);
        assertEquals(0, flipped.get());
        assertEquals(1, flopped.get());
    }

    @Test
    void shouldSwitchOnOff() {
        trigger.value(true);
        trigger.value(false);
        trigger.value(true);
        trigger.value(false);
        assertEquals(2, flipped.get());
        assertEquals(2, flopped.get());
    }

}