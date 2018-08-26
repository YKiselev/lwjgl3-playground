package com.github.ykiselev.spi;

import com.github.ykiselev.services.Services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ClassUtils {

    public static Class<?> forName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class not found: " + name, e);
        }
    }

    /**
     * Loads specified class and creates an instance.
     *
     * @param name     the name of traget class or specific {@link Factory} implementation.
     * @param services the {@link Services} to pass to {@link Factory}.
     * @param <T>      the type of class.
     * @return the instance of class.
     */
    public static <T> T create(String name, Services services) {
        return create(forName(name), services);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<?> clazz, Services services) {
        final T instance;
        if (Factory.class.isAssignableFrom(clazz)) {
            final Factory<T> factory = newInstance((Class<Factory<T>>) clazz);
            instance = factory.create(services);
        } else {
            instance = newInstance((Class<T>) clazz, services);
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(ctor -> match(ctor, args))
                .findFirst()
                .map(ctor -> {
                    try {
                        return (T) ctor.newInstance(args);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalArgumentException("Unable to create instance of " + clazz, e);
                    }
                }).orElseGet(() -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new IllegalArgumentException("Unable to create instance of " + clazz, e);
                    }
                });
    }

    private static boolean match(Constructor<?> ctor, Object... args) {
        final Class<?>[] types = ctor.getParameterTypes();
        if (types.length != args.length) {
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            final Class<?> type = types[i];
            if (arg == null && type.isPrimitive()) {
                return false;
            }
            if (!type.isInstance(arg)) {
                return false;
            }
        }
        return true;
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
