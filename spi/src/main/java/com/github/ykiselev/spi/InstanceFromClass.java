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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class InstanceFromClass<T> implements Supplier<T> {

    private final Supplier<Class<?>> delegate;

    private final Services services;

    public InstanceFromClass(Supplier<Class<?>> delegate, Services services) {
        this.delegate = requireNonNull(delegate);
        this.services = requireNonNull(services);
    }

    public InstanceFromClass(Class clazz, Services services) {
        this(() -> clazz, services);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        final Class<?> clazz = delegate.get();
        final Object instance;
        if (Factory.class.isAssignableFrom(clazz)) {
            instance = newInstance((Class<Factory>) clazz).create(services);
        } else {
            instance = newInstance(clazz, services);
        }
        return (T) instance;
    }

    private static Object newInstance(Class<?> clazz, Object... args) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(ctor -> match(ctor, args))
                .findFirst()
                .map(ctor -> {
                    try {
                        return (Object) ctor.newInstance(args);
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

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
