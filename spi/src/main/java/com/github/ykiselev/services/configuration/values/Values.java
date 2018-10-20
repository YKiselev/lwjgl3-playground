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

import java.util.Objects;
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

    public static final class WiredString extends ConfigValue {

        private final Supplier<String> getter;

        private final Consumer<String> setter;

        public WiredString(String name, boolean persisted, Supplier<String> getter, Consumer<String> setter) {
            super(name, persisted);
            this.getter = requireNonNull(getter);
            this.setter = setter;
        }

        public String value() {
            return getter.get();
        }

        public void value(String value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public void fromObject(Object value) {
            setString(Objects.toString(value, null));
        }

        @Override
        public String toString() {
            return value();
        }

        @Override
        public void setString(String value) {
            value(value);
        }

        @Override
        public Object boxed() {
            return value();
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }

    public static final class WiredBoolean extends ConfigValue {

        private final BooleanSupplier getter;

        private final BooleanConsumer setter;

        public WiredBoolean(String name, boolean persisted, BooleanSupplier getter, BooleanConsumer setter) {
            super(name, persisted);
            this.getter = requireNonNull(getter);
            this.setter = setter;
        }

        public boolean value() {
            return getter.getAsBoolean();
        }

        public void value(boolean value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public void fromObject(Object value) {
            if (value instanceof Boolean) {
                value((Boolean) value);
            } else {
                setString(Objects.toString(value, null));
            }
        }

        @Override
        public String toString() {
            return Boolean.toString(value());
        }

        @Override
        public void setString(String value) {
            value(Boolean.parseBoolean(value));
        }

        @Override
        public Object boxed() {
            return value();
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }

    public static final class WiredLong extends ConfigValue {

        private final LongFormat format;

        private final LongSupplier getter;

        private final LongConsumer setter;

        public LongFormat format() {
            return format;
        }

        public WiredLong(String name, boolean persisted, LongFormat format, LongSupplier getter, LongConsumer setter) {
            super(name, persisted);
            this.getter = requireNonNull(getter);
            this.setter = setter;
            this.format = requireNonNull(format);
        }

        public long value() {
            return getter.getAsLong();
        }

        public void value(long value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public void fromObject(Object value) {
            if (value instanceof Number) {
                value(((Number) value).longValue());
            } else {
                setString(Objects.toString(value, null));
            }
        }

        @Override
        public String toString() {
            return format().toString(value());
        }

        @Override
        public void setString(String value) {
            value(format().parseLong(value));
        }

        @Override
        public Object boxed() {
            return format.boxed(value());
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }

    public static final class WiredDouble extends ConfigValue {

        private final DoubleSupplier getter;

        private final DoubleConsumer setter;

        public WiredDouble(String name, boolean persisted, DoubleSupplier getter, DoubleConsumer setter) {
            super(name, persisted);
            this.getter = getter;
            this.setter = setter;
        }

        public double value() {
            return getter.getAsDouble();
        }

        public void value(double value) {
            if (setter != null) {
                setter.accept(value);
            }
        }

        @Override
        public void fromObject(Object value) {
            if (value instanceof Number) {
                value(((Number) value).doubleValue());
            } else {
                setString(Objects.toString(value, null));
            }
        }

        @Override
        public String toString() {
            return Double.toString(value());
        }

        @Override
        public void setString(String value) {
            value(Double.parseDouble(value));
        }

        @Override
        public Object boxed() {
            return value();
        }

        @Override
        public boolean isReadOnly() {
            return setter != null;
        }
    }
}
