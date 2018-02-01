package com.github.ykiselev.caching;

import com.github.ykiselev.collections.NodeList;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WeightingCachedReferences<V> implements CachedReferences<V> {

    private final int maxTotalWeight;

    private final ToIntFunction<V> scales;

    private final Consumer<V> evictionConsumer;

    private final NodeList<WeightedReference> list = new NodeList<>();

    private int totalWeight;

    /**
     * Primary ctor.
     *
     * @param maxTotalWeight   the maximum total weight of items in cache
     * @param scales           the scaling function to assign weight to new item
     * @param evictionConsumer the consumer to be called when item is evicted from cache
     */
    public WeightingCachedReferences(int maxTotalWeight, ToIntFunction<V> scales, Consumer<V> evictionConsumer) {
        this.maxTotalWeight = maxTotalWeight;
        this.scales = requireNonNull(scales);
        this.evictionConsumer = requireNonNull(evictionConsumer);
    }

    /**
     * Constructor which uses {@link ClosingConsumer} to process evicted items.
     *
     * @param maxTotalWeight the maximum total weight of items in cache
     * @param scales         the consumer to be called when item is evicted from cache
     */

    public WeightingCachedReferences(int maxTotalWeight, ToIntFunction<V> scales) {
        this(maxTotalWeight, scales, new ClosingConsumer<>());
    }

    @Override
    public Cached<V> put(V value) {
        final int weight = scales.applyAsInt(
                requireNonNull(value)
        );
        totalWeight += weight;
        return touch(
                new WeightedReference(
                        value,
                        weight
                )
        );
    }

    @Override
    public synchronized void clear() {
        while (list.tail() != null) {
            evictionConsumer.accept(
                    list.remove(list.tail()).value
            );
        }
    }

    private synchronized WeightedReference touch(WeightedReference ref) {
        list.remove(ref);
        list.addFirst(ref);
        while (list.tail() != ref && totalWeight > maxTotalWeight) {
            evict(list.tail());
        }
        return ref;
    }

    private synchronized void evict(WeightedReference ref) {
        list.remove(ref);
        evictionConsumer.accept(ref.value);
        ref.value = null;
        totalWeight -= ref.weight;
    }

    private class WeightedReference extends NodeList.Node<WeightedReference> implements Cached<V> {

        int weight;

        V value;

        WeightedReference(V referent, int weight) {
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

        @Override
        public void free() {
            evict(this);
        }
    }
}
