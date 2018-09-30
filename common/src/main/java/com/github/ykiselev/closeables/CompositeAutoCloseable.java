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

package com.github.ykiselev.closeables;

import java.util.Arrays;
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
        try {
            for (AutoCloseable subscription : closeables) {
                subscription.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
