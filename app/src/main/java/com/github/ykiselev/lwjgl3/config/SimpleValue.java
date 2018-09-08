package com.github.ykiselev.lwjgl3.config;

/**
 * This class an hold 4 types of value at once (string, long, double and boolean). When one of four set methods
 * is called only it's direct underlying field is assigned while three other fields are lazily populated on demand when
 * appropriate get method is called.
 * Field {@code flag} is used to track which value field was initialized.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class SimpleValue extends ConfigValue {

    private static final int F_STRING = 1;

    private static final int F_LONG = 1 << 1;

    private static final int F_DOUBLE = 1 << 2;

    private static final int F_BOOLEAN = 1 << 3;

    private int flags;

    private String stringValue;

    private long longValue;

    private double doubleValue;

    private boolean booleanValue;

    private ValueType type;

    String stringValue() {
        return stringValue;
    }

    long longValue() {
        return longValue;
    }

    double doubleValue() {
        return doubleValue;
    }

    boolean booleanValue() {
        return booleanValue;
    }

    @Override
    synchronized String getString() {
        if ((flags & F_STRING) == 0) {
            stringValue = type.getString(this);
            flags |= F_STRING;
        }
        return stringValue;
    }

    @Override
    synchronized boolean getBoolean() {
        if ((flags & F_BOOLEAN) == 0) {
            booleanValue = type.getBoolean(this);
            flags |= F_BOOLEAN;
        }
        return booleanValue;
    }

    @Override
    int getInt() {
        final long raw = getLong();
        if (raw < Integer.MIN_VALUE || raw > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value " + raw + " can not be represented as int!");
        }
        return (int) raw;
    }

    @Override
    synchronized long getLong() {
        if ((flags & F_LONG) == 0) {
            longValue = type.getLong(this);
            flags |= F_LONG;
        }
        return longValue;
    }

    @Override
    float getFloat() {
        final double raw = getDouble();
        if (raw < Float.MIN_VALUE && raw > Float.MAX_VALUE) {
            throw new IllegalArgumentException("Value " + raw + " can not be represented as float!");
        }
        return (float) raw;
    }

    @Override
    synchronized double getDouble() {
        if ((flags & F_DOUBLE) == 0) {
            doubleValue = type.getDouble(this);
            flags |= F_DOUBLE;
        }
        return doubleValue;
    }

    @Override
    synchronized void setString(String value) {
        stringValue = value;
        flags = F_STRING;
        type = ValueType.STRING;
    }

    @Override
    synchronized void setBoolean(boolean value) {
        booleanValue = value;
        flags = F_BOOLEAN;
        type = ValueType.BOOLEAN;
    }

    @Override
    void setInt(int value) {
        setLong(value);
    }

    @Override
    synchronized void setLong(long value) {
        longValue = value;
        flags = F_LONG;
        type = ValueType.LONG;
    }

    @Override
    void setFloat(float value) {
        setDouble(value);
    }

    @Override
    synchronized void setDouble(double value) {
        doubleValue = value;
        flags = F_DOUBLE;
        type = ValueType.DOUBLE;
    }
}
