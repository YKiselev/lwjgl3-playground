package com.github.ykiselev.services.configuration.values;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface LongValue extends ConfigValue {

    long value();

    void value(long value);

    @Override
    default String getString() {
        return Long.toString(value());
    }

    @Override
    default void setString(String value) {
        value(Long.parseLong(value));
    }

    @Override
    default Object boxed() {
        return value();
    }
}
