package com.github.ykiselev.spi;

import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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

    @Test
    void shouldLoadAll() {
        ServiceLoader loader = new ServiceLoader(
                ConfigFactory.load("test.conf")
                        .getConfig("services")
        );
        assertArrayEquals(
                new Map.Entry[]{
                        new SimpleImmutableEntry<>(Greeter.class, GreeterImpl.class)
                },
                loader.loadAll().toArray()
        );
    }
}