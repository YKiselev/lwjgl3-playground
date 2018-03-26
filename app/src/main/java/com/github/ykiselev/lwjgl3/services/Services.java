package com.github.ykiselev.lwjgl3.services;

import java.util.Optional;
import java.util.function.Supplier;

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
     * @param clazz    the service class
     * @param instance the service instance
     * @param <T>      the service type
     */
    <T> void add(Class<T> clazz, T instance);

    /**
     * Removes service from registry. Note that method {@link AutoCloseable#close()} is not called upon removal
     * even if service implements {@link AutoCloseable}.
     *
     * @param clazz    the service class
     * @param instance the service instance
     * @param <T>      the service type
     */
    <T> void remove(Class<T> clazz, Object instance);

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
                .orElseThrow(
                        () -> new ServiceNotFoundException(clazz.toString())
                );
    }

    /**
     * Tries to resolve service instance by class.
     *
     * @param clazz the service class
     * @param <T>   the service type
     * @return an {@code Optional} with service instance or empty {@code Optional} if service is not found
     */
    <T> Optional<T> tryResolve(Class<T> clazz);

    /**
     * Tries to resolve service instance by class. If there is no such service then value returned by supplier is registered as a service and returned.
     * Implementation is expected to do this atomically so that two instances would never be created if multiple threads calls this method simultaneously.
     *
     * @param clazz the service class
     * @param <T>   the service type
     * @return service instance
     */
    <T> T resolveOrAdd(Class<T> clazz, Supplier<T> supplier);

    /**
     * Tries to remove service implementing {@link Removable} interface. Service will be removed if call to it's
     * {@link Removable#canBeRemoved()} method returns {@code true}.
     *
     * @param <T>   the service type
     * @param clazz the service class
     * @return the removed instance if call to {@link Removable#canBeRemoved()} returned {@code true} or {@code null} otherwise
     */
    <T extends Removable> Optional<T> tryRemove(Class<T> clazz);
}
