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

package com.github.ykiselev.common.cow;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * Container of value which may be not thread-safe by itself but may be safely used from multiple threads if all
 * modifications are done via creation of fresh copy using method {@link CopyOnModify#modify(UnaryOperator)}.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CopyOnModify<V> {

    private static final VarHandle VH;

    private volatile V value;

    static {
        try {
            VH = MethodHandles.lookup().findVarHandle(CopyOnModify.class, "value", Object.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    /**
     * Note: It is user's responsibility to not modify returned value!
     *
     * @return the current value
     */
    public V value() {
        return value;
    }

    /**
     * Primary ctor.
     *
     * @param value the initial value.
     * @throws NullPointerException if supplied value is {@code null}.
     */
    public CopyOnModify(V value) throws NullPointerException {
        this.value = requireNonNull(value);
    }

    /**
     * Creates new value from current using supplied operator and then sets it using {@link VarHandle#compareAndSet(Object...)} method.
     * This process may be repeated several times until CAS returns {@code true}.
     *
     * @param operator the operator to create new distinct copy of stored value. It is an error to return the same instance!
     * @return the previous value
     * @throws IllegalStateException if new value is the same instance as before
     */
    public V modify(UnaryOperator<V> operator) throws IllegalStateException {
        for (; ; ) {
            final V before = this.value;
            final V after = operator.apply(before);
            if (before == after) {
                throw new IllegalStateException("New value cannot be the same instance as previous!");
            }
            if (VH.compareAndSet(this, before, after)) {
                return before;
            }
        }
    }
}
