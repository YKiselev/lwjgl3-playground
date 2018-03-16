package com.github.ykiselev.proxy;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AutoCloseableProxy {

    public static <T extends AutoCloseable> T create(T target, Class<T> clazz, Consumer<T> onClose) {
        requireNonNull(onClose);
        return clazz.cast(
                Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        new Class[]{clazz},
                        (proxy, method, args) -> {
                            if (args == null && "close".equals(method.getName())) {
                                onClose.accept(target);
                                return null;
                            }
                            return method.invoke(target, args);
                        }
                )
        );
    }
}
