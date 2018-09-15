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