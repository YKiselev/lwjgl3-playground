package com.github.ykiselev.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@Disabled
class ParallelRunnerTest {

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldRunInParallel() throws Exception {
        Supplier<Runnable> s = () -> {
            System.out.println("Preparing from " + Thread.currentThread());
            sleep(ThreadLocalRandom.current().nextLong(100, 250));
            return () -> {
                System.out.println("Running in " + Thread.currentThread());
                sleep(ThreadLocalRandom.current().nextLong(100, 250));
            };
        };
        ParallelRunner.fromRunnable(3, s)
                .call();
    }
}