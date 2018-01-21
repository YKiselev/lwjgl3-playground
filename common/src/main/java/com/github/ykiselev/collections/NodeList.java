package com.github.ykiselev.collections;

/**
 * Simple double linked list with ability to add/remove list nodes (instead of values) and thus to use custom node class.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class NodeList<N extends NodeList.Node<N>> {

    private N head;

    private N tail;

    public N head() {
        return head;
    }

    public N tail() {
        return tail;
    }

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
    public static abstract class Node<N extends Node<N>> {

        N next;

        N prev;

        public final N next() {
            return next;
        }

        public final N prev() {
            return prev;
        }
    }

    /**
     * Base node class
     *
     * @param <V> the type of node's value
     */
    public static class ImmutableNode<V> extends Node<ImmutableNode<V>> {

        private final V value;

        public V value() {
            return value;
        }

        ImmutableNode(V value) {
            this.value = value;
        }
    }

}
