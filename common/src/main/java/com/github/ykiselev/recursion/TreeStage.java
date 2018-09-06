package com.github.ykiselev.recursion;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Stateful tree stage object.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class TreeStage<I, O> implements Callable<Collection<TreeStage<I, O>>> {

    private final Supplier<Collection<TreeStage<I, O>>> supplier;

    private final Function<Collection<TreeStage<I, O>>, O> transformation;

    private Collection<TreeStage<I, O>> stages;

    private O result;

    public O result() {
        return result;
    }

    public TreeStage(Supplier<Collection<TreeStage<I, O>>> supplier, Function<Collection<TreeStage<I, O>>, O> transformation) {
        this.supplier = requireNonNull(supplier);
        this.transformation = requireNonNull(transformation);
    }

    @Override
    public Collection<TreeStage<I, O>> call() {
        return stages = supplier.get();
    }

    public void collectResult() {
        result = transformation.apply(stages);
    }
}

