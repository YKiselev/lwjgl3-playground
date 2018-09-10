package com.github.ykiselev.services.configuration.values;

import com.github.ykiselev.common.BooleanConsumer;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Values {

    private Values() {
    }

    public static final class SimpleString implements StringValue {

        private volatile String value;

        public SimpleString() {
        }

        public SimpleString(String value) {
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

    public static final class WiredString implements StringValue {

        private final Supplier<String> getter;

        private final Consumer<String> setter;

        public WiredString(Supplier<String> getter, Consumer<String> setter) {
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

    public static final class SimpleBoolean implements BooleanValue {

        private volatile boolean value;

        public SimpleBoolean() {
        }

        public SimpleBoolean(boolean value) {
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

    public static final class WiredBoolean implements BooleanValue {

        private final BooleanSupplier getter;

        private final BooleanConsumer setter;

        public WiredBoolean(BooleanSupplier getter, BooleanConsumer setter) {
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

    public static final class SimpleLong implements LongValue {

        private volatile long value;

        public SimpleLong() {
        }

        public SimpleLong(long value) {
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

    public static final class WiredLong implements LongValue {

        private final LongSupplier getter;

        private final LongConsumer setter;

        public WiredLong(LongSupplier getter, LongConsumer setter) {
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

    public static final class SimpleDouble implements DoubleValue {

        private volatile double value;

        public SimpleDouble() {
        }

        public SimpleDouble(double value) {
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

    public static final class WiredDouble implements DoubleValue {

        private final DoubleSupplier getter;

        private final DoubleConsumer setter;

        public WiredDouble(DoubleSupplier getter, DoubleConsumer setter) {
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

    public static <T extends ConfigValue> T simpleValue(Class<T> clazz) {
        final ConfigValue result;
        if (clazz == StringValue.class) {
            result = new SimpleString();
        } else if (clazz == LongValue.class) {
            result = new SimpleLong();
        } else if (clazz == DoubleValue.class) {
            result = new SimpleDouble();
        } else if (clazz == BooleanValue.class) {
            result = new SimpleBoolean();
        } else {
            throw new IllegalArgumentException("Unknown type: " + clazz);
        }
        return clazz.cast(result);
    }

    public static Object toSimpleValue(Object value) {
        final Object result;
        if (value == null || value instanceof String) {
            result = new SimpleString((String) value);
        } else if (value instanceof Long) {
            result = new SimpleLong((long) value);
        } else if (value instanceof Integer) {
            result = new SimpleLong((int) value);
        } else if (value instanceof Double) {
            result = new SimpleDouble((double) value);
        } else if (value instanceof Float) {
            result = new SimpleDouble((float) value);
        } else if (value instanceof Boolean) {
            result = new SimpleBoolean((boolean) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value);
        }
        return result;
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
            result = value;
        }
        return result;
    }
}
