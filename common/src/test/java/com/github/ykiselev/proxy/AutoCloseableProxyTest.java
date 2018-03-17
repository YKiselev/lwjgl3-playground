package com.github.ykiselev.proxy;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AutoCloseableProxyTest {

    private interface B extends AutoCloseable {

        @Override
        void close();
    }

    private static final class A implements AutoCloseable {

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return 123;
        }
    }

    private static final class C implements B {

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return 123;
        }
    }

    @Test
    void shouldProxy1() throws Exception {
        final AtomicBoolean flag = new AtomicBoolean();
        final AutoCloseable proxy = AutoCloseableProxy.create(new A(), AutoCloseable.class, o -> flag.set(true));
        assertEquals(123, proxy.hashCode());
        proxy.close();
        assertTrue(flag.get());
    }

    @Test
    void shouldProxy2() {
        final AtomicBoolean flag = new AtomicBoolean();
        final B proxy = AutoCloseableProxy.create(new C(), B.class, o -> flag.set(true));
        assertEquals(123, proxy.hashCode());
        proxy.close();
        assertTrue(flag.get());
    }
}