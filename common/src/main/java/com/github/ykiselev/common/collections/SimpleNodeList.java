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

package com.github.ykiselev.common.collections;

/**
 * Simple double linked list with ability to add/remove list nodes (instead of values) and thus to use custom node class.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SimpleNodeList<N extends SimpleNodeList.AbstractNode<N>> implements NodeList<N> {

    private N head;

    private N tail;

    @Override
    public N head() {
        return head;
    }

    @Override
    public N tail() {
        return tail;
    }

    @Override
    public N addFirst(N ref) {
        ref.next = head;
        if (head != null) {
            head.prev = ref;
        }
        head = ref;
        if (tail == null) {
            tail = ref;
        }
        return ref;
    }

    @Override
    public N addLast(N ref) {
        ref.prev = tail;
        if (tail != null) {
            tail.next = ref;
        }
        tail = ref;
        if (head == null) {
            head = ref;
        }
        return ref;
    }

    @Override
    public N remove(N ref) {
        final N next = ref.next;
        final N prev = ref.prev;
        if (next != null) {
            next.prev = prev;
        }
        if (prev != null) {
            prev.next = next;
        }
        if (ref == head) {
            head = next;
        }
        if (ref == tail) {
            tail = prev;
        }
        ref.prev = ref.next = null;
        return ref;
    }

    /**
     * Base node class without actual payload.
     *
     * @param <N> the type of node's implementing class
     */
    public static abstract class AbstractNode<N extends AbstractNode<N>> implements Node<N> {

        N next;

        N prev;

        @Override
        public final N next() {
            return next;
        }

        @Override
        public final N prev() {
            return prev;
        }
    }

    /**
     * Base node class
     *
     * @param <V> the type of node's value
     */
    public static class ImmutableNode<V> extends AbstractNode<ImmutableNode<V>> {

        private final V value;

        public V value() {
            return value;
        }

        ImmutableNode(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "ImmutableNode{" +
                    "value=" + value +
                    '}';
        }
    }

}
