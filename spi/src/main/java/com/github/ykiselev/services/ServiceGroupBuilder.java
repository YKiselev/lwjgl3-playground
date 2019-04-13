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

import com.github.ykiselev.common.closeables.CompositeAutoCloseable;

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
