/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private static abstract class AbstractValue implements ConfigValue {

        private final boolean persisted;

        AbstractValue(boolean persisted) {
            this.persisted = persisted;
        }

        @Override
        public boolean isPersisted() {
            return persisted;
        }
    }

    public static final class SimpleString extends AbstractValue implements StringValue {

        private volatile String value;

        public SimpleString(boolean persisted, String value) {
            super(persisted);
            this.value = value;
        }

        public SimpleString() {
            super(false);
        }

        public SimpleString(String value) {
            this(false, value);
        }

        public SimpleString(WiredString src) {
            this(src.isPersisted(), src.value());
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

    public static final class WiredString extends AbstractValue implements StringValue {

        private final Supplier<String> getter;

        private final Consumer<String> setter;

        public WiredString(boolean persisted, Supplier<String> getter, Consumer<String> setter) {
            super(persisted);
            this.getter = requireNonNull(getter);
            this.setter = setter;
        }

        @Override
        public String value() {
            return getter.get();
        }

        @Override
        public void value(String value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }

    public static final class SimpleBoolean extends AbstractValue implements BooleanValue {

        private volatile boolean value;

        public SimpleBoolean(boolean persisted, boolean value) {
            super(persisted);
            this.value = value;
        }

        public SimpleBoolean() {
            super(false);
        }

        public SimpleBoolean(boolean value) {
            this(false, value);
        }

        public SimpleBoolean(WiredBoolean src) {
            this(src.isPersisted(), src.value());
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

    public static final class WiredBoolean extends AbstractValue implements BooleanValue {

        private final BooleanSupplier getter;

        private final BooleanConsumer setter;

        public WiredBoolean(boolean persisted, BooleanSupplier getter, BooleanConsumer setter) {
            super(persisted);
            this.getter = requireNonNull(getter);
            this.setter = setter;
        }

        @Override
        public boolean value() {
            return getter.getAsBoolean();
        }

        @Override
        public void value(boolean value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }

    public static final class SimpleLong extends AbstractValue implements LongValue {

        private volatile long value;

        public SimpleLong(boolean persisted, long value) {
            super(persisted);
            this.value = value;
        }

        public SimpleLong() {
            super(false);
        }

        public SimpleLong(long value) {
            this(false, value);
        }

        public SimpleLong(WiredLong src) {
            this(src.isPersisted(), src.value());
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

    public static final class WiredLong extends AbstractValue implements LongValue {

        private final LongSupplier getter;

        private final LongConsumer setter;

        public WiredLong(boolean persisted, LongSupplier getter, LongConsumer setter) {
            super(persisted);
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public long value() {
            return getter.getAsLong();
        }

        @Override
        public void value(long value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }

    public static final class SimpleDouble extends AbstractValue implements DoubleValue {

        private volatile double value;

        public SimpleDouble(boolean persisted, double value) {
            super(persisted);
            this.value = value;
        }

        public SimpleDouble() {
            super(false);
        }

        public SimpleDouble(double value) {
            this(false, value);
        }

        public SimpleDouble(WiredDouble src) {
            this(src.isPersisted(), src.value());
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

    public static final class WiredDouble extends AbstractValue implements DoubleValue {

        private final DoubleSupplier getter;

        private final DoubleConsumer setter;

        public WiredDouble(boolean persisted, DoubleSupplier getter, DoubleConsumer setter) {
            super(persisted);
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public double value() {
            return getter.getAsDouble();
        }

        @Override
        public void value(double value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
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
            result = new SimpleString((WiredString) value);
        } else if (value instanceof WiredBoolean) {
            result = new SimpleBoolean((WiredBoolean) value);
        } else if (value instanceof WiredLong) {
            result = new SimpleLong((WiredLong) value);
        } else if (value instanceof WiredDouble) {
            result = new SimpleDouble((WiredDouble) value);
        } else {
            result = value;
        }
        return result;
    }
}
