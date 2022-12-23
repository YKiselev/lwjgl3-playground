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

package com.github.ykiselev.common.closeables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CompositeAutoCloseable implements AutoCloseable {

    private final AutoCloseable[] closeables;

    public CompositeAutoCloseable(AutoCloseable... closeables) {
        this.closeables = closeables.clone();
    }

    @Override
    public void close() {
        Builder.close(closeables);
    }

    public CompositeAutoCloseable and(AutoCloseable value) {
        final AutoCloseable[] array = Arrays.copyOf(closeables, closeables.length + 1);
        array[closeables.length] = value;
        return new CompositeAutoCloseable(array);
    }

    public CompositeAutoCloseable with(UnaryOperator<CompositeAutoCloseable> value) {
        return value.apply(this);
    }

    public CompositeAutoCloseable reverse() {
        final AutoCloseable[] tmp = new AutoCloseable[closeables.length];
        for (int i = 0; i <= closeables.length / 2; i++) {
            tmp[tmp.length - i - 1] = closeables[i];
            tmp[i] = closeables[closeables.length - i - 1];
        }
        return new CompositeAutoCloseable(tmp);
    }

    /**
     * Mutable builder for composite auto closeables class.
     * Use it in try-with-resource and post adding all elements call method {@link Builder#build()} to detach collected items
     * from builder instance. That way in case of an error inside try-ith-resource block all collected closeables
     * will be closed by this builder and in case of success all closeables will be transferred to composite auto closeables
     * instance to further use.
     */
    public static final class Builder implements AutoCloseable {

        private final List<AutoCloseable> closeables = new ArrayList<>();

        public Builder add(AutoCloseable c) {
            closeables.add(c);
            return this;
        }

        /**
         * Upon the call to this method all collected closeables are transferred to built instance and builder's list
         * of closeables is cleared.
         *
         * @return the new composite auto closeables object with all collected closeables.
         */
        public CompositeAutoCloseable build() {
            final AutoCloseable[] ca = closeables.toArray(new AutoCloseable[0]);
            closeables.clear();
            return new CompositeAutoCloseable(ca);
        }

        @Override
        public void close() {
            if (!closeables.isEmpty()) {
                close(closeables.toArray(new AutoCloseable[0]));
            }
        }

        static void close(AutoCloseable[] closeables) {
            List<Exception> exceptions = null;
            for (AutoCloseable subscription : closeables) {
                try {
                    subscription.close();
                } catch (Exception e) {
                    if (exceptions == null) {
                        exceptions = new ArrayList<>();
                    }
                    exceptions.add(e);
                }
            }
            if (exceptions != null) {
                RuntimeException ex = new RuntimeException("Failed to close all delegates!");
                exceptions.forEach(ex::addSuppressed);
                throw ex;
            }
        }
    }
}
