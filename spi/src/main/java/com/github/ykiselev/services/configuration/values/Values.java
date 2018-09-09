package com.github.ykiselev.services.configuration.values;

import com.github.ykiselev.common.BooleanConsumer;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Values {

    private Values() {
    }

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
    }

    static final class WiredString implements StringValue {

        private final Supplier<String> getter;

        private final Consumer<String> setter;

        WiredString(Supplier<String> getter, Consumer<String> setter) {
            this.getter = requireNonNull(getter);
            this.setter = requireNonNull(setter);
        }

        @Override
        public String value() {
            return getter.get();
        }

        @Override
        public void value(String value) {
            setter.accept(value);
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
    }

    static final class WiredBoolean implements BooleanValue {

        private final BooleanSupplier getter;

        private final BooleanConsumer setter;

        WiredBoolean(BooleanSupplier getter, BooleanConsumer setter) {
            this.getter = requireNonNull(getter);
            this.setter = requireNonNull(setter);
        }

        @Override
        public boolean value() {
            return getter.getAsBoolean();
        }

        @Override
        public void value(boolean value) {
            setter.accept(value);
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
    }

    static final class WiredLong implements LongValue {

        private final LongSupplier getter;

        private final LongConsumer setter;

        WiredLong(LongSupplier getter, LongConsumer setter) {
            this.getter = requireNonNull(getter);
            this.setter = requireNonNull(setter);
        }

        @Override
        public long value() {
            return getter.getAsLong();
        }

        @Override
        public void value(long value) {
            setter.accept(value);
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
    }

    static final class WiredDouble implements DoubleValue {

        private final DoubleSupplier getter;

        private final DoubleConsumer setter;

        WiredDouble(DoubleSupplier getter, DoubleConsumer setter) {
            this.getter = requireNonNull(getter);
            this.setter = requireNonNull(setter);
        }

        @Override
        public double value() {
            return getter.getAsDouble();
        }

        @Override
        public void value(double value) {
            setter.accept(value);
        }
    }

    public static final class ConstantList {

        private final List<?> list;

        public List<?> list() {
            return list;
        }

        public ConstantList(List<?> list) {
            this.list = requireNonNull(list);
        }

        public <T> List<T> toUniformList(Class<T> itemClass) {
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

    public static ConfigValue toSimpleValue(ConfigValue value) {
        final ConfigValue result;
        if (value instanceof WiredString) {
            result = new SimpleString(((WiredString) value).value());
        } else if (value instanceof WiredBoolean) {
            result = new SimpleBoolean(((WiredBoolean) value).value());
        } else if (value instanceof WiredLong) {
            result = new SimpleLong(((WiredLong) value).value());
        } else if (value instanceof WiredDouble) {
            result = new SimpleDouble(((WiredDouble) value).value());
        } else {
            throw new IllegalArgumentException("Unknown value type: " + value);
        }
        return result;
    }
}
