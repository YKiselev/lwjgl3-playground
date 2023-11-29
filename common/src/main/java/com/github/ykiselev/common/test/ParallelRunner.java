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

package com.github.ykiselev.common.test;

import com.github.ykiselev.common.ThrowingRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Utility class to run N code blocks in parallel. Each code block may be {@link Callable} or {@link Runnable} created by
 * provided suppliers. Any unhandled exception thrown from code block will be added to the list of suppressed exceptions
 * of type {@link IllegalStateException} thrown at the end of {@link ParallelRunner#call()}.
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

    /**
     * Runs supplied tasks specified number of iterations. Before each iteration tasks are synchronized internally using {@link CyclicBarrier}.
     *
     * @return the list of collected task results.
     * @throws IllegalStateException if some of the supplied tasks has thrown exception or task's {@link Future#get()} throws exception.
     */
    @Override
    public Collection<Collection<V>> call() throws IllegalStateException {
        final List<Throwable> throwables = new CopyOnWriteArrayList<>();
        @SuppressWarnings("unchecked") final FutureTask<Collection<V>>[] tasks =
                Arrays.stream(suppliers)
                        .map(supplier -> (Callable<Collection<V>>) () -> threadLoop(supplier, throwables::add))
                        .map(FutureTask::new)
                        .toArray(FutureTask[]::new);
        final Thread[] threads = Arrays.stream(tasks)
                .map(Thread::new)
                .peek(Thread::start)
                .toArray(Thread[]::new);
        waitForThreads(threads, throwables);
        if (!throwables.isEmpty()) {
            final IllegalStateException ex = new IllegalStateException();
            throwables.forEach(ex::addSuppressed);
            throw ex;
        }
        return collectResult(tasks);
    }

    private Collection<Collection<V>> collectResult(FutureTask<Collection<V>>[] tasks) {
        final List<Collection<V>> result = new ArrayList<>();
        for (FutureTask<Collection<V>> task : tasks) {
            try {
                result.add(task.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }
        return result;
    }

    private void waitForThreads(Thread[] threads, List<Throwable> throwables) {
        for (; ; ) {
            // If some threads have failed with exceptions we need to interrupt all other threads otherwise they will stick on barrier!
            if (!throwables.isEmpty()) {
                Arrays.stream(threads)
                        .forEach(Thread::interrupt);
            }
            // Count completed threads
            int completed = 0;
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    completed++;
                }
            }
            if (threads.length == completed) {
                break;
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Collection<V> threadLoop(Supplier<Callable<V>> supplier, Consumer<Throwable> onThrowable) {
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

    private static Supplier<Callable<Void>> callable(Supplier<ThrowingRunnable> s) {
        return () -> () -> {
            s.get().run();
            return null;
        };
    }

    public static Runnable fromRunnable(int iterations, Supplier<ThrowingRunnable> supplier) {
        return fromRunnable(iterations, Runtime.getRuntime().availableProcessors(), supplier);
    }

    @SuppressWarnings("unchecked")
    public static Runnable fromRunnable(int iterations, int parallelism, Supplier<ThrowingRunnable> supplier) {
        return () -> new ParallelRunner<>(
                iterations,
                IntStream.range(0, parallelism)
                        .mapToObj(v -> callable(supplier))
                        .toArray(Supplier[]::new)
        ).call();
    }

    public static <V> Callable<Collection<Collection<V>>> fromCallable(int iterations, Supplier<Callable<V>> supplier) {
        return fromCallable(iterations, Runtime.getRuntime().availableProcessors(), supplier);
    }

    @SuppressWarnings("unchecked")
    public static <V> Callable<Collection<Collection<V>>> fromCallable(int iterations, int parallelism, Supplier<Callable<V>> supplier) {
        return new ParallelRunner<>(
                iterations,
                IntStream.range(0, parallelism)
                        .mapToObj(v -> supplier)
                        .toArray(Supplier[]::new)
        );
    }
}
