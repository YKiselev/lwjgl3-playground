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

package com.github.ykiselev.cow;

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

    private volatile V item;

    static {
        try {
            VH = MethodHandles.lookup().findVarHandle(CopyOnModify.class, "item", Object.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    /**
     * Note: It is user's responsibility to not modify returned value!
     *
     * @return the current value of item
     */
    public V item() {
        return item;
    }

    /**
     * Primary ctor.
     *
     * @param item the initial value.
     * @throws NullPointerException if supplied value is {@code null}.
     */
    public CopyOnModify(V item) throws NullPointerException {
        this.item = requireNonNull(item);
    }

    /**
     * Creates new value from current using supplied operator and then sets it using {@link VarHandle#compareAndSet(Object...)} method.
     * This process may be repeated several times until CAS returns {@code true}. It is important for supplied operator
     * to create fresh value each time it is invoked!
     *
     * @param operator the operator to create new distinct copy of stored value. It is an error to return the same value!
     */
    public void modify(UnaryOperator<V> operator) {
        for (; ; ) {
            final V before = this.item;
            final V after = operator.apply(before);
            if (before == after) {
                throw new IllegalStateException("New value should not be identical to previous!");
            }
            if (VH.compareAndSet(this, before, after)) {
                break;
            }
        }
    }
}
