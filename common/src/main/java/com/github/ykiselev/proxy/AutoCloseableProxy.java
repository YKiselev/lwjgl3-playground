package com.github.ykiselev.proxy;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Creates a proxy which implements {@link AutoCloseable} interface (irrespective to the original class).
 * When proxy's {@link AutoCloseable#close()} method is called original object instance is passed to the specified {@code onClose} consumer.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AutoCloseableProxy {

    /**
     * @param target     the target instance to proxify.
     * @param interfaces the interfaces to implement by proxy.
     * @param onClose    the consumer to call upon proxy closing.
     * @param <T>        the type parameter
     * @return the proxy of specified type (implementing {@link AutoCloseable} interface).
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(T target, Class<?>[] interfaces, Consumer<T> onClose) {
        requireNonNull(onClose);
        final Class<?>[] ifaces;
        if (AutoCloseable.class.isInstance(target)) {
            ifaces = interfaces;
        } else {
            ifaces = Arrays.copyOf(interfaces, interfaces.length + 1);
            ifaces[ifaces.length - 1] = AutoCloseable.class;
        }
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                ifaces,
                (proxy, method, args) -> {
                    if (args == null && "close".equals(method.getName())) {
                        onClose.accept(target);
                        return null;
                    }
                    return method.invoke(target, args);
                }
        );
    }

    public static <T> T create(T target, Consumer<T> onClose) {
        return create(
                target,
                target.getClass().getInterfaces(),
                onClose
        );
    }

    public static <T> T create(T target, Class<?> clazz, Consumer<T> onClose) {
        if (clazz.isInterface()) {
            return create(target, new Class[]{clazz}, onClose);
        }
        return create(target, onClose);
    }
}
