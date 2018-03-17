package com.github.ykiselev.proxy;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AutoCloseableProxy {

    @SuppressWarnings("unchecked")
    public static <T extends AutoCloseable> T create(T target, Class<?>[] interfaces, Consumer<T> onClose) {
        requireNonNull(onClose);
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                (proxy, method, args) -> {
                    if (args == null && "close".equals(method.getName())) {
                        onClose.accept(target);
                        return null;
                    }
                    return method.invoke(target, args);
                }
        );
    }

    public static <T extends AutoCloseable> T create(T target, Consumer<T> onClose) {
        return create(
                target,
                target.getClass().getInterfaces(),
                onClose
        );
    }

    public static <T extends AutoCloseable> T create(T target, Class clazz, Consumer<T> onClose) {
        if (clazz.isInterface()) {
            return create(target, new Class[]{clazz}, onClose);
        }
        return create(target, onClose);
    }
}
