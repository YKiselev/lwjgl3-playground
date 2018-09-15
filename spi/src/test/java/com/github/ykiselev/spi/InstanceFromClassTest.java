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

package com.github.ykiselev.spi;

import com.github.ykiselev.services.Services;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class InstanceFromClassTest {

    public static class F implements Factory<Integer> {

        public F() {
        }

        @Override
        public Integer create(Services services) {
            return 123;
        }
    }

    public static class A {

        private final Services services;

        public A(Services services) {
            this.services = services;
        }
    }

    private Services services = Mockito.mock(Services.class);

    @Test
    void shouldCreateFromClass() {
        assertTrue(
                new InstanceFromClass<>(String.class, services)
                        .get() instanceof String
        );
    }

    @Test
    void shouldCreateUsingCtorWithArg() {
        assertTrue(
                new InstanceFromClass<>(A.class, services)
                        .get() instanceof A
        );
    }

    @Test
    void shouldCreateFromFactory() {
        assertEquals(
                123,
                new InstanceFromClass<>(F.class, services)
                        .get()
        );
    }

}