package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DelegatesTest {

    @Test
    void shouldAddAndRemove() throws Exception {
        final Delegates<String> array = new Delegates<>(new String[0]);
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
        final Delegates<Integer> array = new Delegates<>(new Integer[0]);
        final AtomicInteger seq = new AtomicInteger();
        final AtomicLong counter = new AtomicLong();
        final Supplier<Runnable> r = () -> () -> {
            List<AutoCloseable> list = IntStream.range(0, 10)
                    .mapToObj(k -> array.add(seq.incrementAndGet()))
                    .collect(Collectors.toList());
            for (Integer integer : array.array()) {
                counter.addAndGet(integer);
            }
            Collections.shuffle(list);
            for (AutoCloseable closeable : list) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ParallelRunner.fromRunnable(1, r)
                .call();
        assertEquals(0, array.array().length);
        System.out.println(counter.get());
    }

    @RepeatedTest(100)
        //@Disabled
    void compareWithCLD() throws Exception {
        final ConcurrentLinkedDeque<Integer> deque = new ConcurrentLinkedDeque<>();
        final AtomicInteger seq = new AtomicInteger();
        final AtomicLong counter = new AtomicLong();
        final Supplier<Runnable> r = () -> () -> {
            List<Integer> ints = IntStream.range(0, 10)
                    .mapToObj(k -> {
                        final Integer v = seq.incrementAndGet();
                        deque.addFirst(v);
                        return v;
                    }).collect(Collectors.toList());
            for (Integer integer : deque) {
                counter.addAndGet(integer);
            }
            Collections.shuffle(ints);
            ints.forEach(deque::remove);
        };
        ParallelRunner.fromRunnable(1, r)
                .call();
        assertTrue(deque.isEmpty());
        System.out.println(counter.get());
    }

    @RepeatedTest(100)
    @Disabled
    void shouldIterate() throws Exception {
        final Delegates<Integer> array = new Delegates<>(new Integer[0]);
        IntStream.range(0, 10)
                .forEach(array::add);
        final AtomicLong counter = new AtomicLong();
        final Supplier<Runnable> r = () -> () -> {
            for (Integer v : array.array()) {
                counter.addAndGet(v);
            }
        };
        ParallelRunner.fromRunnable(1000, r)
                .call();
        System.out.println("counter=" + counter.get());
    }

    @RepeatedTest(100)
    @Disabled
    void iterateCLD() throws Exception {
        final ConcurrentLinkedDeque<Integer> deque = new ConcurrentLinkedDeque<>();
        IntStream.range(0, 10)
                .forEach(deque::addFirst);
        final AtomicLong counter = new AtomicLong();
        final Supplier<Runnable> r = () -> () -> {
            for (Integer v : deque) {
                counter.addAndGet(v);
            }
        };
        ParallelRunner.fromRunnable(1000, r)
                .call();
        System.out.println("counter=" + counter.get());
    }
}