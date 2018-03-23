package cob.github.ykiselev.lwjgl3.services;

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
}
