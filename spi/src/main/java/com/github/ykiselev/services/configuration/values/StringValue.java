package com.github.ykiselev.services.configuration.values;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface StringValue extends ConfigValue {

    String value();

    void value(String value);

    @Override
    default String getString() {
        return value();
    }

    @Override
    default void setString(String value) {
        value(value);
    }

    @Override
    default Object boxed() {
        return value();
    }
}
