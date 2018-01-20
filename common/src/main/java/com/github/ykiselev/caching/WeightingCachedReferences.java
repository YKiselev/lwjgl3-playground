package com.github.ykiselev.caching;

import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WeightingCachedReferences<V> implements CachedReferences<V> {

    private final int maxTotalWeight;

    private final ToIntFunction<V> scales;

    private Node head;

    private Node tail;

    private int totalWeight;

    public WeightingCachedReferences(int maxTotalWeight, ToIntFunction<V> scales) {
        this.maxTotalWeight = maxTotalWeight;
        this.scales = requireNonNull(scales);
    }

    @Override
    public Cached<V> put(V value) {
        final int weight = scales.applyAsInt(
                requireNonNull(value)
        );
        totalWeight += weight;
        return touch(
                new Node(
                        value,
                        weight
                )
        );
    }

    private synchronized Node touch(Node ref) {
        detach(ref);
        ref.next = head;
        if (head != null) {
            head.prev = ref;
        }
        head = ref;
        if (tail == null) {
            tail = ref;
        } else {
            while (tail != ref && totalWeight > maxTotalWeight) {
                evict(tail);
            }
        }
        return ref;
    }

    private void evict(Node ref) {
        detach(ref).value = null;
        totalWeight -= ref.weight;
    }

    private Node detach(Node ref) {
        final Node next = ref.next;
        final Node prev = ref.prev;
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

    private class Node extends Cached<V> {

        int weight;

        Node next;

        Node prev;

        V value;

        Node(V referent, int weight) {
            this.value = referent;
            this.weight = weight;
        }

        @Override
        public V get() {
            if (value != null) {
                touch(this);
            }
            return value;
        }
    }
}
