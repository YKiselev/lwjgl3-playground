package com.github.ykiselev.caching;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class WeightingCachedReferencesTest {

    private final CachedReferences<Integer> cache = new WeightingCachedReferences<>(10, v -> v);

    @Test
    void shouldEvict() {
        Cached<Integer> r1 = cache.put(3);
        Cached<Integer> r2 = cache.put(5);
        Cached<Integer> r3 = cache.put(2);
        assertEquals((Integer) 3, r1.get());
        assertEquals((Integer) 5, r2.get());
        assertEquals((Integer) 2, r3.get());
        Cached<Integer> r4 = cache.put(1);
        assertNull(r1.get());
        assertEquals((Integer) 5, r2.get());
        assertEquals((Integer) 2, r3.get());
        assertEquals((Integer) 1, r4.get());
        Cached<Integer> r5 = cache.put(3);
        assertNull(r2.get());
        assertEquals((Integer) 2, r3.get());
        assertEquals((Integer) 1, r4.get());
        assertEquals((Integer) 3, r5.get());
        Cached<Integer> r6 = cache.put(10);
        assertNull(r3.get());
        assertNull(r4.get());
        assertNull(r5.get());
        assertEquals((Integer) 10, r6.get());
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            final List<Future> list = IntStream.range(0, 100)
                    .mapToObj(v -> service.submit(
                            () -> threadLoop(v)
                    )).collect(Collectors.toList());

            list.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            service.shutdown();
            service.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private void threadLoop(int value) {
        final Cached<Integer> v = cache.put(value);
        for (int i = 0; i < 25; i++) {
            final Integer integer = v.get();
            if (integer == null) {
                System.out.println("[" + value + "] Got null on iteration " + i);
                break;
            } else if (integer != value) {
                throw new IllegalStateException("Expected " + value + " got " + integer);
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}