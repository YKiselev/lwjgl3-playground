package com.github.ykiselev.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Utility class to run N code blocks in parallel. Each code block may be {@link Callable} or {@link Runnable} created by
 * provided suppliers. Any unhandled exception thrown from code block will be added to the list of suppressed exceptions
 * of {@link IllegalStateException} thrown at the end of {@link ParallelRunner#call()}.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ParallelRunner<V> implements Callable<Collection<Collection<V>>> {

    private final Supplier<Callable<V>>[] suppliers;

    private final int iterations;

    private final CyclicBarrier barrier;

    @SafeVarargs
    public ParallelRunner(int iterations, Supplier<Callable<V>>... suppliers) {
        this.suppliers = suppliers.clone();
        this.iterations = iterations;
        this.barrier = new CyclicBarrier(suppliers.length);
    }

    @Override
    public Collection<Collection<V>> call() throws Exception {
        final List<Throwable> throwables = new CopyOnWriteArrayList<>();
        @SuppressWarnings("unchecked") final FutureTask<Collection<V>>[] tasks =
                Arrays.stream(suppliers)
                        .map(supplier -> (Callable<Collection<V>>) () -> loop(supplier, throwables::add))
                        .map(FutureTask::new)
                        .toArray(FutureTask[]::new);
        final Thread[] threads = Arrays.stream(tasks)
                .map(Thread::new)
                .peek(Thread::start)
                .toArray(Thread[]::new);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!throwables.isEmpty()) {
            final IllegalStateException ex = new IllegalStateException();
            throwables.forEach(ex::addSuppressed);
            throw ex;
        }
        final List<Collection<V>> result = new ArrayList<>();
        for (FutureTask<Collection<V>> task : tasks) {
            result.add(task.get());
        }
        return result;
    }

    private Collection<V> loop(Supplier<Callable<V>> supplier, Consumer<Throwable> onThrowable) {
        final List<V> results = new ArrayList<>();
        try {
            final Callable<V> callable = supplier.get();
            for (int i = 0; i < iterations; i++) {
                barrier.await();
                results.add(callable.call());
            }
        } catch (Throwable t) {
            onThrowable.accept(t);
        }
        return results;
    }

    private static Supplier<Callable<Void>> callable(Supplier<Runnable> s) {
        return () -> () -> {
            s.get().run();
            return null;
        };
    }

    @SuppressWarnings("unchecked")
    public static Callable<Void> fromRunnable(int iterations, Supplier<Runnable> supplier) {
        return () -> {
            new ParallelRunner<>(
                    iterations,
                    IntStream.range(0, Runtime.getRuntime().availableProcessors())
                            .mapToObj(v -> callable(supplier))
                            .toArray(Supplier[]::new)
            ).call();
            return null;
        };
    }

    @SuppressWarnings("unchecked")
    public static <V> Callable<Collection<Collection<V>>> fromCallable(int iterations, Supplier<Callable<V>> supplier) {
        return new ParallelRunner(
                iterations,
                IntStream.range(0, Runtime.getRuntime().availableProcessors())
                        .mapToObj(v -> supplier)
                        .toArray(Supplier[]::new)
        );
    }

}
