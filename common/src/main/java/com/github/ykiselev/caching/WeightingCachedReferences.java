package com.github.ykiselev.caching;

import com.github.ykiselev.collections.NodeList;

import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WeightingCachedReferences<V> implements CachedReferences<V> {

    private final int maxTotalWeight;

    private final ToIntFunction<V> scales;

    private final NodeList<WeightedReference> list = new NodeList<>();

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
                new WeightedReference(
                        value,
                        weight
                )
        );
    }

    private synchronized WeightedReference touch(WeightedReference ref) {
        list.remove(ref);
        list.addFirst(ref);
        while (list.tail() != ref && totalWeight > maxTotalWeight) {
            evict(list.tail());
        }
        return ref;
    }

    private void evict(WeightedReference ref) {
        list.remove(ref);
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
    }
}
