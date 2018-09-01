package com.github.ykiselev.collections;

import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CopyOnWriteArrayTest {

    @Test
    void shouldAddAndRemove() throws Exception {
        final CopyOnWriteArray<String> array = new CopyOnWriteArray<>(new String[0]);
        AutoCloseable c1 = array.add("a");
        AutoCloseable c2 = array.add("b");
        AutoCloseable c3 = array.add("c");
        assertArrayEquals(new String[]{"a", "b", "c"}, array.array());
        c1.close();
        assertArrayEquals(new String[]{"b", "c"}, array.array());
        c3.close();
        assertArrayEquals(new String[]{"b"}, array.array());
        c2.close();
        assertArrayEquals(new String[0], array.array());
    }

    @RepeatedTest(100)
    void shouldBeThreadSafe() throws Exception {
        final CopyOnWriteArray<Integer> array = new CopyOnWriteArray<>(new Integer[0]);
        final AtomicInteger seq = new AtomicInteger();
        final Supplier<Runnable> r = () -> () -> {
            List<AutoCloseable> list = IntStream.range(0, 10)
                    .mapToObj(k -> array.add(seq.incrementAndGet()))
                    .collect(Collectors.toList());
            Collections.shuffle(list);
            for (AutoCloseable closeable : list) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ParallelRunner.fromRunnable(100, r)
                .call();
        assertEquals(0, array.array().length);
    }

    @RepeatedTest(100)
    void compareWithCLD() throws Exception {
        final ConcurrentLinkedDeque<Integer> deque = new ConcurrentLinkedDeque<>();
        final AtomicInteger seq = new AtomicInteger();
        final Supplier<Runnable> r = () -> () -> {
            List<Integer> ints = IntStream.range(0, 10)
                    .mapToObj(k -> {
                        final Integer v = seq.incrementAndGet();
                        deque.addFirst(v);
                        return v;
                    }).collect(Collectors.toList());
            Collections.shuffle(ints);
            ints.forEach(deque::remove);
        };
        ParallelRunner.fromRunnable(100, r)
                .call();
        assertTrue(deque.isEmpty());
    }

}