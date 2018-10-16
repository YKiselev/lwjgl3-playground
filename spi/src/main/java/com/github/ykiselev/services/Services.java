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

package com.github.ykiselev.services;

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
        final T svc = tryResolve(clazz);
        if (svc == null) {
            throw new ServiceNotFoundException(clazz);
        }
        return svc;
    }

    /**
     * Tries to resolve service instance by class.
     *
     * @param <T>   the service type
     * @param clazz the service class
     * @return service instance or {@code null} if not service is found
     */
    <T> T tryResolve(Class<T> clazz);
}
