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