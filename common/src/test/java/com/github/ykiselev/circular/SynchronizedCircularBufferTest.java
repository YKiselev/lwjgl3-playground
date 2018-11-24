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

package com.github.ykiselev.circular;

import com.github.ykiselev.common.ThrowingRunnable;
import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class SynchronizedCircularBufferTest {

    private final CircularBuffer<Integer> ints = new SynchronizedCircularBuffer<>(
            new ArrayCircularBuffer<>(Integer.class, 100)
    );

    @Test
    public void shouldBeThreadSafe() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        Supplier<ThrowingRunnable> s = () ->
                () -> ints.write(counter.incrementAndGet());
        ParallelRunner.fromRunnable(1000, s)
                .call();
        assertEquals(100, ints.count());
        Integer[] dest = new Integer[ints.count()];
        ints.copyTo(dest);
        int prev = dest[0];
        for (int i = 1; i < 100; i++) {
            int v = dest[i];
            assertEquals(v, prev + 1);
            prev = v;
        }
    }
}