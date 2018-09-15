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

package com.github.ykiselev.lazy;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Lazy {

    private Lazy() {
    }

    public static <V> Supplier<V> of(Supplier<V> delegate) {
        return new Supplier<>() {

            private Supplier<V> s = () -> {
                final V value = delegate.get();
                this.s = () -> value;
                return value;
            };

            @Override
            public V get() {
                return s.get();
            }
        };
    }

    public static <V> Supplier<V> sync(Supplier<V> delegate) {
        return new Supplier<V>() {

            private final Object lock = new Object();

            private volatile Supplier<V> s = delegate;

            @Override
            public V get() {
                if (s == delegate) {
                    synchronized (lock) {
                        if (s == delegate) {
                            final V value = delegate.get();
                            s = () -> value;
                        }
                    }
                }
                return s.get();
            }
        };
    }

    public static IntSupplier of(IntSupplier delegate) {
        return new IntSupplier() {

            private IntSupplier s = () -> {
                final int value = delegate.getAsInt();
                this.s = () -> value;
                return value;
            };

            @Override
            public int getAsInt() {
                return s.getAsInt();
            }
        };
    }

    public static IntSupplier sync(IntSupplier delegate) {
        return new IntSupplier() {

            private final Object lock = new Object();

            private volatile IntSupplier s = delegate;

            @Override
            public int getAsInt() {
                if (s == delegate) {
                    synchronized (lock) {
                        if (s == delegate) {
                            final int value = delegate.getAsInt();
                            s = () -> value;
                        }
                    }
                }
                return s.getAsInt();
            }
        };
    }

    public static DoubleSupplier of(DoubleSupplier delegate) {
        return new DoubleSupplier() {

            private DoubleSupplier s = () -> {
                final double value = delegate.getAsDouble();
                this.s = () -> value;
                return value;
            };

            @Override
            public double getAsDouble() {
                return s.getAsDouble();
            }
        };
    }

    public static DoubleSupplier sync(DoubleSupplier delegate) {
        return new DoubleSupplier() {

            private final Object lock = new Object();

            private volatile DoubleSupplier s = delegate;

            @Override
            public double getAsDouble() {
                if (s == delegate) {
                    synchronized (lock) {
                        if (s == delegate) {
                            final double value = delegate.getAsDouble();
                            s = () -> value;
                        }
                    }
                }
                return s.getAsDouble();
            }
        };
    }

    public static LongSupplier of(LongSupplier delegate) {
        return new LongSupplier() {

            private LongSupplier s = () -> {
                final long value = delegate.getAsLong();
                this.s = () -> value;
                return value;
            };

            @Override
            public long getAsLong() {
                return s.getAsLong();
            }
        };
    }

    public static LongSupplier sync(LongSupplier delegate) {
        return new LongSupplier() {

            private final Object lock = new Object();

            private volatile LongSupplier s = delegate;

            @Override
            public long getAsLong() {
                if (s == delegate) {
                    synchronized (lock) {
                        if (s == delegate) {
                            final long value = delegate.getAsLong();
                            s = () -> value;
                        }
                    }
                }
                return s.getAsLong();
            }
        };
    }

    public static BooleanSupplier of(BooleanSupplier delegate) {
        return new BooleanSupplier() {

            private BooleanSupplier s = () -> {
                final boolean value = delegate.getAsBoolean();
                this.s = () -> value;
                return value;
            };

            @Override
            public boolean getAsBoolean() {
                return s.getAsBoolean();
            }
        };
    }

    public static BooleanSupplier sync(BooleanSupplier delegate) {
        return new BooleanSupplier() {

            private final Object lock = new Object();

            private volatile BooleanSupplier s = delegate;

            @Override
            public boolean getAsBoolean() {
                if (s == delegate) {
                    synchronized (lock) {
                        if (s == delegate) {
                            final boolean value = delegate.getAsBoolean();
                            s = () -> value;
                        }
                    }
                }
                return s.getAsBoolean();
            }
        };
    }

}
