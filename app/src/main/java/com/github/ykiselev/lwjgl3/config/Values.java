package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.configuration.BooleanValue;
import com.github.ykiselev.services.configuration.ConfigValue;
import com.github.ykiselev.services.configuration.DoubleValue;
import com.github.ykiselev.services.configuration.LongValue;
import com.github.ykiselev.services.configuration.StringValue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class Values {

    static final class SimpleString implements StringValue {

        private volatile String value;

        SimpleString() {
        }

        SimpleString(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public void value(String value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return value;
        }

        @Override
        public void setString(String value) {
            value(value);
        }

        @Override
        public Object boxed() {
            return value;
        }
    }

    static final class SimpleBoolean implements BooleanValue {

        private volatile boolean value;

        SimpleBoolean() {
        }

        SimpleBoolean(boolean value) {
            this.value = value;
        }

        @Override
        public boolean value() {
            return value;
        }

        @Override
        public void value(boolean value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return Boolean.toString(value);
        }

        @Override
        public void setString(String value) {
            value(Boolean.parseBoolean(value));
        }

        @Override
        public Object boxed() {
            return value;
        }
    }

    static final class SimpleLong implements LongValue {

        private volatile long value;

        SimpleLong() {
        }

        SimpleLong(long value) {
            this.value = value;
        }

        @Override
        public long value() {
            return value;
        }

        @Override
        public void value(long value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return Long.toString(value);
        }

        @Override
        public void setString(String value) {
            value(Long.parseLong(value));
        }

        @Override
        public Object boxed() {
            return value;
        }
    }

    static final class SimpleDouble implements DoubleValue {

        private volatile double value;

        SimpleDouble() {
        }

        SimpleDouble(double value) {
            this.value = value;
        }

        @Override
        public double value() {
            return value;
        }

        @Override
        public void value(double value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return Double.toString(value);
        }

        @Override
        public void setString(String value) {
            value(Double.parseDouble(value));
        }

        @Override
        public Object boxed() {
            return value;
        }
    }

    static final class ConstantList {

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

    static final class Section {

        private final Set<String> names;

        public Set<String> names() {
            return names;
        }

        Section(Set<String> names) {
            this.names = names;
        }
    }

    public static <T extends ConfigValue> T create(Class<T> clazz) {
        final ConfigValue result;
        if (clazz == StringValue.class) {
            result = new Values.SimpleString();
        } else if (clazz == LongValue.class) {
            result = new Values.SimpleLong();
        } else if (clazz == DoubleValue.class) {
            result = new Values.SimpleDouble();
        } else if (clazz == BooleanValue.class) {
            result = new Values.SimpleBoolean();
        } else {
            throw new IllegalArgumentException("Unknown type: " + clazz);
        }
        return clazz.cast(result);
    }
}
