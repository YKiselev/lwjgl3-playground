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

package com.github.ykiselev.lifetime;

import com.github.ykiselev.wrap.Wrap;

import java.util.function.BiConsumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
public final class SharedResource<V> implements AutoCloseable {

    private final Ref<V> ref;

    public SharedResource(V value, BiConsumer<SharedResource<V>, V> disposer) {
        this.ref = new CountedRef<>(
                value,
                v -> disposer.accept(this, v)
        );
    }

    /**
     * Increments reference counter and returns instance wrapped in wrap which upon calling {@link Wrap#close()} will decrement reference counter.
     *
     * @return object instance wrapped in reference-counting wrap.
     */
    public Wrap<V> share() {
        final V value = ref.newRef();
        if (value != null) {
            return new Wrap<V>(value) {
                @Override
                public void close() {
                    ref.release();
                }
            };
        }
        return null;
    }

    @Override
    public void close() {
        ref.close();
    }
}
