package com.github.ykiselev.lwjgl3.config;

import java.math.BigDecimal;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public enum ValueType {

    STRING() {
        @Override
        String getString(SimpleValue value) {
            return value.stringValue();
        }

        @Override
        long getLong(SimpleValue value) {
            return toNumber(value.stringValue())
                    .longValue();
        }

        @Override
        double getDouble(SimpleValue value) {
            return toNumber(value.stringValue())
                    .doubleValue();
        }

        @Override
        boolean getBoolean(SimpleValue value) {
            final String string = value.stringValue();
            if (string == null) {
                return false;
            }
            if (string.equals("true")) {
                return true;
            }
            if (string.equals("false")) {
                return false;
            }
            return getLong(value) != 0;
        }
    },
    LONG() {
        @Override
        String getString(SimpleValue value) {
            return Long.toString(value.longValue());
        }

        @Override
        long getLong(SimpleValue value) {
            return value.longValue();
        }

        @Override
        double getDouble(SimpleValue value) {
            final long raw = value.longValue();
            if (raw < Double.MIN_VALUE || raw > Double.MAX_VALUE) {
                throw new IllegalArgumentException("Value " + raw + " can not be represented as double!");
            }
            return raw;
        }

        @Override
        boolean getBoolean(SimpleValue value) {
            return value.longValue() != 0;
        }
    },
    DOUBLE() {
        @Override
        String getString(SimpleValue value) {
            return Double.toString(value.getDouble());
        }

        @Override
        long getLong(SimpleValue value) {
            final double raw = value.doubleValue();
            if (raw < Long.MIN_VALUE && raw > Long.MAX_VALUE) {
                throw new IllegalArgumentException("Value " + raw + " can not be represented as long!");
            }
            return (long) raw;
        }

        @Override
        double getDouble(SimpleValue value) {
            return value.doubleValue();
        }

        @Override
        boolean getBoolean(SimpleValue value) {
            final double raw = value.doubleValue();
            return Double.isFinite(raw) && !Double.isNaN(raw) && raw != 0;
        }
    },
    BOOLEAN() {
        @Override
        String getString(SimpleValue value) {
            return Boolean.toString(value.booleanValue());
        }

        @Override
        long getLong(SimpleValue value) {
            return value.booleanValue() ? 1 : 0;
        }

        @Override
        double getDouble(SimpleValue value) {
            return value.booleanValue() ? 1 : 0;
        }

        @Override
        boolean getBoolean(SimpleValue value) {
            return value.booleanValue();
        }
    };

    private static final Number ZERO = 0L;

    protected Number toNumber(String value) {
        if (value == null) {
            return ZERO;
        }
        return new BigDecimal(value);
    }

    abstract String getString(SimpleValue value);

    abstract long getLong(SimpleValue value);

    abstract double getDouble(SimpleValue value);

    abstract boolean getBoolean(SimpleValue value);

}
