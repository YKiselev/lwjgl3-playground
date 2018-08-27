package com.github.ykiselev.spi;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ClassFromName implements Supplier<Class<?>> {

    private final String name;

    public ClassFromName(String name) {
        this.name = requireNonNull(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<?> get() {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class not found: " + name, e);
        }
    }

    public static Class<?> get(String name) {
        return new ClassFromName(name).get();
    }
}
