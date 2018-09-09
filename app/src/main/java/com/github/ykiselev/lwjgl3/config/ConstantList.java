package com.github.ykiselev.lwjgl3.config;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

final class ConstantList {

    private final List<?> list;

    List<?> list() {
        return list;
    }

    ConstantList(List<?> list) {
        this.list = requireNonNull(list);
    }

    <T> List<T> toUniformList(Class<T> itemClass) {
        return list.stream()
                .map(itemClass::cast)
                .collect(Collectors.toList());
    }
}

