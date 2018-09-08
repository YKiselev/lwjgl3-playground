package com.github.ykiselev.lwjgl3.config;

import java.util.Collection;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
abstract class ConfigValue {

    String getString() {
        throw new UnsupportedOperationException();
    }

    boolean getBoolean() {
        throw new UnsupportedOperationException();
    }

    int getInt() {
        throw new UnsupportedOperationException();
    }

    long getLong() {
        throw new UnsupportedOperationException();
    }

    float getFloat() {
        throw new UnsupportedOperationException();
    }

    double getDouble() {
        throw new UnsupportedOperationException();
    }

    Collection<String> getStringList() {
        throw new UnsupportedOperationException();
    }

    void setString(String value) {
        throw new UnsupportedOperationException();
    }

    void setBoolean(boolean value) {
        throw new UnsupportedOperationException();
    }

    void setInt(int value) {
        throw new UnsupportedOperationException();
    }

    void setLong(long value) {
        throw new UnsupportedOperationException();
    }

    void setFloat(float value) {
        throw new UnsupportedOperationException();
    }

    void setDouble(double value) {
        throw new UnsupportedOperationException();
    }

}
