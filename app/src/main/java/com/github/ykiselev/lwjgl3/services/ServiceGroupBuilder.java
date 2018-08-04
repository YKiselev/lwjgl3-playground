package com.github.ykiselev.lwjgl3.services;

import com.github.ykiselev.closeables.CompositeAutoCloseable;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ServiceGroupBuilder {

    private final Services services;

    private final CompositeAutoCloseable ac;

    public ServiceGroupBuilder(Services services, CompositeAutoCloseable ac) {
        this.services = requireNonNull(services);
        this.ac = requireNonNull(ac);
    }

    public ServiceGroupBuilder(Services services) {
        this(services, new CompositeAutoCloseable());
    }

    public <T> ServiceGroupBuilder add(Class<T> clazz, T instance) {
        return new ServiceGroupBuilder(
                services,
                ac.and(services.add(clazz, instance))
        );
    }

    /**
     * @return the reversed chain of {@link AutoCloseable}'s
     */
    public CompositeAutoCloseable build() {
        return ac.reverse();
    }

}
