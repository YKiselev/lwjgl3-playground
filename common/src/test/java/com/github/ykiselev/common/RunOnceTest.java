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