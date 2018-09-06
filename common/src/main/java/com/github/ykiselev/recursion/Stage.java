package com.github.ykiselev.recursion;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Stage<V> implements Callable<Stage<V>> {

    private final V value;

    private final Supplier<Stage<V>> nextStage;

    public V value() {
        return value;
    }

    public Stage(V value, Supplier<Stage<V>> nextStage) {
        this.value = value;
        this.nextStage = nextStage;
    }

    @Override
    public Stage<V> call() {
        if (nextStage != null) {
            return nextStage.get();
        }
        return null;
    }
}
