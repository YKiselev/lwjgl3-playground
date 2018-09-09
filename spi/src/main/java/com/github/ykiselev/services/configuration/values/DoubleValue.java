package com.github.ykiselev.services.configuration.values;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface DoubleValue extends ConfigValue {

    double value();

    void value(double value);

    @Override
    default String getString() {
        return Double.toString(value());
    }

    @Override
    default void setString(String value) {
        value(Double.parseDouble(value));
    }

    @Override
    default Object boxed() {
        return value();
    }
}
