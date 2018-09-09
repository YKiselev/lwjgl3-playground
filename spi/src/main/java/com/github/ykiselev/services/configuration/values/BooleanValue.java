package com.github.ykiselev.services.configuration.values;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface BooleanValue extends ConfigValue {

    boolean value();

    void value(boolean value);

    @Override
    default String getString() {
        return Boolean.toString(value());
    }

    @Override
    default void setString(String value) {
        value(Boolean.parseBoolean(value));
    }

    @Override
    default Object boxed() {
        return value();
    }
}
