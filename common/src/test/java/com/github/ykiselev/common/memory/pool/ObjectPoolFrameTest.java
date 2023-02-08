package com.github.ykiselev.common.memory.pool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectPoolFrameTest {

    @BeforeEach
    void setUp() {
        ObjectPoolFrame.clear();
    }

    @Test
    void shouldFillPoolAndRetainValues() {
        try (var frame = ObjectPoolFrame.push(int[].class, () -> new int[1])) {
            frame.allocate()[0] = 1;
            frame.allocate()[0] = 2;

            try (var nestedFrame = ObjectPoolFrame.push(int[].class, () -> new int[1])) {
                nestedFrame.allocate()[0] = 3;
                nestedFrame.allocate()[0] = 4;
                assertEquals(2, nestedFrame.size());
                assertEquals(2, nestedFrame.index());
            }

            frame.allocate()[0] = 5;
            frame.allocate()[0] = 6;
            assertEquals(4, frame.size());
            assertEquals(4, frame.index());
        }

        try (var frame = ObjectPoolFrame.push(int[].class, () -> new int[1])) {
            assertEquals(1, frame.allocate()[0]);
            assertEquals(2, frame.allocate()[0]);
            assertEquals(4, frame.size());
            assertEquals(2, frame.index());

            try (var nestedFrame = ObjectPoolFrame.push(int[].class, () -> new int[1])) {
                assertEquals(3, nestedFrame.allocate()[0]);
                assertEquals(4, nestedFrame.allocate()[0]);
            }
            assertEquals(5, frame.allocate()[0]);
            assertEquals(6, frame.allocate()[0]);
        }
    }
}