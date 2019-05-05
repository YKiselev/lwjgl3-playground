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

package com.github.ykiselev.playground.init;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.05.2019
 */
final class Context {

    private static final class Node {

        final Class<?> key;

        final Object value;

        final Node next;

        Node(Class<?> key, Object value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private final Node head;

    Context(Node head) {
        this.head = head;
    }

    Context(Class<?> key, Object value) {
        this(new Node(key, value, null));
    }

    Context with(Class<?> key, Object value) {
        return new Context(new Node(key, value, head));
    }

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> clazz) {
        Node node = this.head;
        while (node != null) {
            if (clazz.equals(node.key)) {
                return (T) requireNonNull(node.value);
            }
            node = node.next;
        }
        throw new IllegalArgumentException("Not found: " + clazz);
    }
}
