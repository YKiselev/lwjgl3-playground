package com.github.ykiselev.tree;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Node<N extends Node<N>> {

    int size();

    N childAt(int index);

    default Stream<N> children() {
        return IntStream.range(0, size())
                .mapToObj(this::childAt);
    }
}
