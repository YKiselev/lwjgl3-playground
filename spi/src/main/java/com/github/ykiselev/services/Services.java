package com.github.ykiselev.services;

import java.util.Optional;

/**
 * Service registry.
 * Note: implementations are expected to call {@link AutoCloseable#close()} for each registered service implementing
 * {@link AutoCloseable} interface in the reversed order of registration.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Services extends AutoCloseable {

    /**
     * Adds service to registry
     *
     * @param <T>      the service type
     * @param clazz    the service class
     * @param instance the service instance
     * @return the service registration handle. When {@link AutoCloseable#close()} is called on that value service instance
     * is unregistered and passed to {@link com.github.ykiselev.closeables.Closeables#close(Object)}.
     */
    <T> AutoCloseable add(Class<T> clazz, T instance);

    /**
     * Resolves service instance by class
     *
     * @param <T>   the service type
     * @param clazz the service class
     * @return the service instance
     * @throws ServiceNotFoundException if service not found
     */
    default <T> T resolve(Class<T> clazz) throws ServiceNotFoundException {
        return tryResolve(clazz)
                .orElseThrow(() -> new ServiceNotFoundException(clazz));
    }

    /**
     * Tries to resolve service instance by class.
     *
     * @param clazz the service class
     * @param <T>   the service type
     * @return an {@code Optional} with service instance or empty {@code Optional} if service is not found
     */
    <T> Optional<T> tryResolve(Class<T> clazz);
}
