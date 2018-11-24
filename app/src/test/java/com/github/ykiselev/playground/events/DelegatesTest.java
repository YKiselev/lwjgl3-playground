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

package com.github.ykiselev.playground.events;

import com.github.ykiselev.common.ThrowingRunnable;
import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class DelegatesTest {

    @Test
    public void shouldAddAndRemove() throws Exception {
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
    public void shouldBeThreadSafe() throws Exception {
        final Delegates<Integer> array = new Delegates<>(new Integer[0]);
        final AtomicInteger seq = new AtomicInteger();
        final AtomicLong counter = new AtomicLong();
        final Supplier<ThrowingRunnable> r = () -> () -> {
            List<AutoCloseable> list = IntStream.range(0, 10)
                    .mapToObj(k -> array.add(seq.incrementAndGet()))
                    .collect(Collectors.toList());
            for (Integer integer : array.array()) {
                counter.addAndGet(integer);
            }
            Collections.shuffle(list);
            for (AutoCloseable closeable : list) {
                closeable.close();
            }
        };
        ParallelRunner.fromRunnable(1, r)
                .call();
        assertEquals(0, array.array().length);
    }
}