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

package com.github.ykiselev.assets;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 14.04.2019
 */
public class DefaultRecipe<K, A, C> implements Recipe<K, A, C> {

    private final K key;

    private final Class<A> type;

    private final C context;

    @Override
    public Class<A> type() {
        return type;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public C context() {
        return context;
    }

    public DefaultRecipe(K key, Class<A> type, C context) {
        this.key = requireNonNull(key);
        this.type = requireNonNull(type);
        this.context = context;
    }

    public static <A, K> Recipe<K, A, Void> of(K key, Class<A> type) {
        return new DefaultRecipe<>(key, type, null);
    }

    public static <A> Recipe<String, A, Void> of(Class<A> type) {
        return new DefaultRecipe<>(type.getName(), type, null);
    }

    public static <A, C> Recipe<String, A, C> of(Class<A> type, C context) {
        return new DefaultRecipe<>(type.getName(), type, null);
    }
}
