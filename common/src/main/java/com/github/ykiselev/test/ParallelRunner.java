package com.github.ykiselev.test;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ParallelRunner implements Runnable {

    private final Supplier<Runnable>[] suppliers;

    private final int iterations;

    private final CyclicBarrier barrier;

    @SafeVarargs
    public ParallelRunner(int iterations, Supplier<Runnable>... suppliers) {
        this.suppliers = suppliers.clone();
        this.iterations = iterations;
        this.barrier = new CyclicBarrier(suppliers.length);
    }

    @Override
    public void run() {
        final Thread[] threads = IntStream.range(0, suppliers.length)
                .mapToObj(v -> new Thread(() -> loop(suppliers[v]), "t#" + v))
                .toArray(Thread[]::new);
        Arrays.stream(threads).forEach(Thread::start);
        Arrays.stream(threads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loop(Supplier<Runnable> supplier) {
        final Runnable runnable = supplier.get();
        for (int i = 0; i < iterations; i++) {
            try {
                barrier.await();
                runnable.run();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
