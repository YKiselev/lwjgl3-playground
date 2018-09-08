package com.github.ykiselev.lwjgl3.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ListValue extends ConfigValue {

    private final List<String> value;

    ListValue(List<String> value) {
        this.value = Collections.unmodifiableList(value);
    }

    @Override
    Collection<String> getStringList() {
        return value;
    }
}
