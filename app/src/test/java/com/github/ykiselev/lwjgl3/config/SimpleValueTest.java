package com.github.ykiselev.lwjgl3.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class SimpleValueTest {

    private final ConfigValue value = new SimpleValue();

    @Test
    void shouldTolerateNullString() {
        value.setString(null);
        assertNull(value.getString());
        assertFalse(value.getBoolean());
        assertEquals(0, value.getInt());
        assertEquals(0, value.getLong());
        assertEquals(0f, value.getFloat());
        assertEquals(0, value.getDouble());
    }

    @Test
    void shouldSetString() {
        value.setString("1.55");
        assertEquals("1.55", value.getString());
        assertTrue(value.getBoolean());
        assertEquals(1, value.getInt());
        assertEquals(1, value.getLong());
        assertEquals(1.55f, value.getFloat());
        assertEquals(1.55, value.getDouble());

        value.setString("true");
        assertTrue(value.getBoolean());

        value.setString("false");
        assertFalse(value.getBoolean());
    }

    @Test
    void shouldSetBoolean() {
        value.setBoolean(true);
        assertTrue(value.getBoolean());
        assertEquals("true", value.getString());
        assertEquals(1, value.getInt());
        assertEquals(1, value.getLong());
        assertEquals(1f, value.getFloat());
        assertEquals(1, value.getDouble());
    }

    @Test
    void shouldSetInt() {
        value.setInt(123);
        assertEquals("123", value.getString());
        assertTrue(value.getBoolean());
        assertEquals(123, value.getInt());
        assertEquals(123, value.getLong());
        assertEquals(123f, value.getFloat());
        assertEquals(123d, value.getDouble());
    }

    @Test
    void shouldSetLong() {
        value.setLong(2);
        assertEquals("2", value.getString());
        assertTrue(value.getBoolean());
        assertEquals(2, value.getInt());
        assertEquals(2, value.getLong());
        assertEquals(2f, value.getFloat());
        assertEquals(2d, value.getDouble());
    }

    @Test
    void shouldSetFloat() {
        value.setFloat(3f);
        assertEquals("3.0", value.getString());
        assertTrue(value.getBoolean());
        assertEquals(3, value.getInt());
        assertEquals(3, value.getLong());
        assertEquals(3f, value.getFloat());
        assertEquals(3d, value.getDouble());
    }

    @Test
    void shouldSetDouble() {
        value.setDouble(4);
        assertEquals("4.0", value.getString());
        assertTrue(value.getBoolean());
        assertEquals(4, value.getInt());
        assertEquals(4, value.getLong());
        assertEquals(4f, value.getFloat());
        assertEquals(4d, value.getDouble());
    }

}