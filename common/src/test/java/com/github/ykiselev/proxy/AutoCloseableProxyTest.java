package com.github.ykiselev.proxy;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
            return 1;
        }
    }

    private static final class C implements B {

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return 2;
        }
    }

    private interface D {

    }

    private static final class E implements D {

        @Override
        public int hashCode() {
            return 3;
        }
    }

    @Test
    void shouldProxy1() throws Exception {
        Consumer<A> consumer = Mockito.mock(Consumer.class);
        final A a = new A();
        final AutoCloseable proxy = AutoCloseableProxy.create(a, AutoCloseable.class, consumer);
        assertEquals(1, proxy.hashCode());
        proxy.close();
        verify(consumer, times(1)).accept(a);
    }

    @Test
    void shouldProxy2() {
        Consumer<B> consumer = Mockito.mock(Consumer.class);
        C c = new C();
        final B proxy = AutoCloseableProxy.create(c, B.class, consumer);
        assertEquals(2, proxy.hashCode());
        proxy.close();
        verify(consumer, times(1)).accept(c);
    }

    @Test
    void shouldProxy3() throws Exception {
        Consumer<D> consumer = Mockito.mock(Consumer.class);
        E e = new E();
        final D proxy = AutoCloseableProxy.create(e, D.class, consumer);
        assertEquals(3, proxy.hashCode());
        ((AutoCloseable)proxy).close();
        verify(consumer, times(1)).accept(e);
    }

}