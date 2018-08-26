package com.github.ykiselev.spi;

import com.github.ykiselev.services.Services;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ServiceLoaderTest {

    public interface Greeter {

        String sayHello(String name);
    }

    public static final class GreeterImpl implements Greeter {

        @Override
        public String sayHello(String name) {
            return "Hello, " + name;
        }
    }

    public interface Sender {

        void send(Object value);
    }

    public static final class SenderImpl implements Sender {

        @Override
        public void send(Object value) {
            // no-op
        }
    }

    public static class SenderFactory implements Factory<Sender> {

        @Override
        public Sender create(Services services) {
            return new SenderImpl();
        }
    }

    public static class Foo {

    }

    public static class FooFactory implements Factory<Foo> {

        @Override
        public Foo create(Services services) {
            return new Foo();
        }
    }

    @Test
    void shouldLoadAll() {
        ServiceLoader loader = new ServiceLoader(
                ConfigFactory.load("test.conf")
                        .getObjectList("services")
        );
        assertArrayEquals(
                new Map.Entry[]{
                        new SimpleImmutableEntry<>(Sender.class, SenderFactory.class),
                        new SimpleImmutableEntry<>(Greeter.class, GreeterImpl.class)
                },
                loader.loadAll().toArray()
        );
    }

    @Test
    void shouldFailWhenIfaceNotImplemented() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceLoader(
                        ConfigFactory.load("wrong1.conf")
                                .getObjectList("services")
                ).loadAll().toArray()
        );
    }

    @Test
    void shouldFailWhenInstanceNotImplementsIface() {
        // Should work
        new ServiceLoader(
                ConfigFactory.load("wrong2.conf")
                        .getObjectList("services")
        ).loadAll().toArray();
        assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceLoader(
                        ConfigFactory.load("wrong2.conf")
                                .getObjectList("services")
                ).loadAll(Mockito.mock(Services.class))
        );
    }
}