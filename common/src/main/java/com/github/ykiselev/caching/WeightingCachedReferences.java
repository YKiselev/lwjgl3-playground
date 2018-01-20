package com.github.ykiselev.caching;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WeightingCachedReferences<V> implements CachedReferences<V> {

    private final AtomicLong sequence = new AtomicLong();

    private final AtomicLong lock = new AtomicLong();

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

    private long nextSequence() {
        for (; ; ) {
            final long seq = sequence.incrementAndGet();
            if (seq <= 0 || seq == Long.MAX_VALUE) {
                sequence.compareAndSet(seq, 0);
            } else {
                return seq;
            }
        }
    }

    private long lock() {
        final long seq = nextSequence();
        int sleep = 0;
        for (; ; ) {
            if (lock.compareAndSet(0, seq)) {
                break;
            }
            sleep++;
            if (sleep > 150) {
                Thread.yield();
//                try {
//                    Thread.sleep(0);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
        return seq;
    }

    private void unlock(long seq) {
        if (!lock.compareAndSet(seq, 0)) {
            throw new IllegalStateException("Lock is not held by owner of sequence value " + seq);
        }
    }

    private synchronized Node touch(Node ref) {
//        final long id = lock();
//        try {
            detach(ref);
            ref.next = head;
            if (head != null) {
                head.prev = ref;
            }
            head = ref;
            if (tail == null) {
                tail = ref;
            } else {
                int k = 0;
                while (tail != ref && totalWeight > maxTotalWeight) {
                    evict(tail);
                    k++;
                }
            }
//        } finally {
//            unlock(id);
//        }
        return ref;
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

    private void evict(Node ref) {
        detach(ref).value = null;
        totalWeight -= ref.weight;
    }

    private class Node extends Cached<V> {

        final int weight;

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
