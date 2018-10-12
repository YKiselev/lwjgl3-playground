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

import java.util.Arrays;
import java.util.List;
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

        private final String name;

        private final boolean persisted;

        AbstractValue(String name, boolean persisted) {
            this.name = requireNonNull(name);
            this.persisted = persisted;
        }

        @Override
        public boolean isPersisted() {
            return persisted;
        }

        @Override
        public String name() {
            return name;
        }
    }

    public static final class SimpleString extends AbstractValue implements StringValue {

        private volatile String value;

        public SimpleString(String name, boolean persisted, String value) {
            super(name, persisted);
            this.value = value;
        }

        public SimpleString(String name) {
            super(name, false);
        }

        public SimpleString(String name, String value) {
            this(name, false, value);
        }

        public SimpleString(WiredString src) {
            this(src.name(), src.isPersisted(), src.value());
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

        public WiredString(String name, boolean persisted, Supplier<String> getter, Consumer<String> setter) {
            super(name, persisted);
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

        public SimpleBoolean(String name, boolean persisted, boolean value) {
            super(name, persisted);
            this.value = value;
        }

        public SimpleBoolean(String name) {
            super(name, false);
        }

        public SimpleBoolean(String name, boolean value) {
            this(name, false, value);
        }

        public SimpleBoolean(WiredBoolean src) {
            this(src.name(), src.isPersisted(), src.value());
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

        public WiredBoolean(String name, boolean persisted, BooleanSupplier getter, BooleanConsumer setter) {
            super(name, persisted);
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

        public SimpleLong(String name, boolean persisted, long value) {
            super(name, persisted);
            this.value = value;
        }

        public SimpleLong(String name) {
            super(name, false);
        }

        public SimpleLong(String name, long value) {
            this(name, false, value);
        }

        public SimpleLong(WiredLong src) {
            this(src.name(), src.isPersisted(), src.value());
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

        public WiredLong(String name, boolean persisted, LongSupplier getter, LongConsumer setter) {
            super(name, persisted);
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

        public SimpleDouble(String name, boolean persisted, double value) {
            super(name, persisted);
            this.value = value;
        }

        public SimpleDouble(String name) {
            super(name, false);
        }

        public SimpleDouble(String name, double value) {
            this(name, false, value);
        }

        public SimpleDouble(WiredDouble src) {
            this(src.name(), src.isPersisted(), src.value());
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

        public WiredDouble(String name, boolean persisted, DoubleSupplier getter, DoubleConsumer setter) {
            super(name, persisted);
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

    public static final class ArrayBasedConstantList extends AbstractValue implements ConstantList {

        private final Object[] values;

        ArrayBasedConstantList(String name, List<?> list) {
            super(name, false);
            this.values = list.toArray();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> toList() {
            return List.of((T[]) values);
        }

        @Override
        public String getString() {
            return Arrays.toString(values);
        }

        @Override
        public void setString(String value) {
            throw new UnsupportedOperationException("Collection is read-only!");
        }

        @Override
        public Object boxed() {
            return List.of(values);
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }
    }

    public static <T extends ConfigValue> T simpleValue(String name, Class<T> clazz) {
        final ConfigValue result;
        if (clazz == StringValue.class) {
            result = new SimpleString(name);
        } else if (clazz == LongValue.class) {
            result = new SimpleLong(name);
        } else if (clazz == DoubleValue.class) {
            result = new SimpleDouble(name);
        } else if (clazz == BooleanValue.class) {
            result = new SimpleBoolean(name);
        } else {
            throw new IllegalArgumentException("Unknown type: " + clazz);
        }
        return clazz.cast(result);
    }

    public static ConfigValue toSimpleValue(String name, Object value) {
        final ConfigValue result;
        if (value == null || value instanceof String) {
            result = new SimpleString(name, (String) value);
        } else if (value instanceof Long) {
            result = new SimpleLong(name, (long) value);
        } else if (value instanceof Integer) {
            result = new SimpleLong(name, (int) value);
        } else if (value instanceof Double) {
            result = new SimpleDouble(name, (double) value);
        } else if (value instanceof Float) {
            result = new SimpleDouble(name, (float) value);
        } else if (value instanceof Boolean) {
            result = new SimpleBoolean(name, (boolean) value);
        } else if (value instanceof List) {
            return new ArrayBasedConstantList(name, (List<?>) value);
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
