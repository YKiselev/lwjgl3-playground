package com.github.ykiselev.common;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class RunOnceTest {

    @Test
    void shouldRunOnce() {
        final Runnable runnable = Mockito.mock(Runnable.class);
        final RunOnce once = new RunOnce(runnable);
        assertFalse(once.wasRun());
        once.run();
        assertTrue(once.wasRun());
        once.run();
        assertTrue(once.wasRun());
        Mockito.verify(runnable).run();
    }

    @Test
    void shouldBeThreadSafe() throws Exception {
        final AtomicLong counter = new AtomicLong();
        final RunOnce once = new RunOnce(counter::incrementAndGet);
        final ExecutorService service = Executors.newFixedThreadPool(16);
        try {
            while (!once.wasRun()) {
                for (int i = 0; i < 16; i++) {
                    service.submit(once);
                }
                assertFalse(counter.get() > 1);
            }
        } finally {
            service.shutdown();
            assertTrue(service.awaitTermination(10, TimeUnit.SECONDS));
        }
    }
}