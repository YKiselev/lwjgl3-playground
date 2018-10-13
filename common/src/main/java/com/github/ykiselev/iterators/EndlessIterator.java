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

package com.github.ykiselev.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class EndlessIterator<T> implements Iterator<T> {

    private final Iterable<T> collection;

    private Iterator<T> it;

    public EndlessIterator(Iterable<T> collection) {
        this.collection = Objects.requireNonNull(collection);
    }

    @Override
    public boolean hasNext() {
        if (it == null || !it.hasNext()) {
            it = collection.iterator();
        }
        return it.hasNext();
    }

    @Override
    public T next() {
        if (hasNext()) {
            return it.next();
        }
        throw new NoSuchElementException();
    }
}
