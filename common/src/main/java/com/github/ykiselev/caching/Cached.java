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

package com.github.ykiselev.caching;

/**
 * Idea here is that instead of getting direct reference to value of type {@code V} user gets instance of this class
 * and uses method {@link Cached#get()} to get actual reference only when it's needed (not saving it anywhere). This
 * gives implementations opportunity to release least recently used underlying objects and thus free system resources.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Cached<V> {

    /**
     * Returns the reference to the underlying object.
     *
     * @return the value reference or {@code null}
     */
    V get();

    /**
     * Evicts this item from cache and releases system resources.
     */
    void evict();
}
