package com.github.ykiselev.lwjgl3.config;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BooleanValue extends ConfigValue {

    private volatile boolean value;

    @Override
    boolean getBoolean() {
        return value;
    }

    @Override
    int getInt() {
        return value ? 1 : 0;
    }

    @Override
    long getLong() {
        return getInt();
    }

    @Override
    float getFloat() {
        return getInt();
    }

    @Override
    double getDouble() {
        return getFloat();
    }

    @Override
    void setBoolean(boolean value) {
        this.value = value;
    }

    @Override
    void setInt(int value) {
        setBoolean(value != 0);
    }

    @Override
    void setLong(long value) {
        setBoolean(value != 0);
    }

    @Override
    void setFloat(float value) {
        setBoolean(value != 0);
    }

    @Override
    void setDouble(double value) {
        setBoolean(value != 0);
    }
}
