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

package com.github.ykiselev.playground.services.config;

import com.github.ykiselev.services.configuration.ConfigurationException.VariableNotFoundException;
import com.github.ykiselev.services.configuration.PersistedConfiguration;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.values.StringValue;
import com.github.ykiselev.services.configuration.values.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@SuppressWarnings("unchecked")
@DisplayName("app config")
class AppConfigTest {

    static class State {

        private int var1 = 1;

        private boolean var2 = true;

        private long var3 = 3L;

        private double var4 = 4d;

        private String var5 = "5";

        int var1() {
            return var1;
        }

        void var1(int var1) {
            this.var1 = var1;
        }

        boolean var2() {
            return var2;
        }

        void var2(boolean var2) {
            this.var2 = var2;
        }

        long var3() {
            return var3;
        }

        void var3(long var3) {
            this.var3 = var3;
        }

        double var4() {
            return var4;
        }

        void var4(double var5) {
            this.var4 = var5;
        }

        String var5() {
            return var5;
        }

        void var5(String var6) {
            this.var5 = var6;
        }
    }

    private Consumer<Map<String, Object>> writer = Mockito.mock(Consumer.class);

    @Nested
    @DisplayName("when filled")
    class WhenFilled {

        PersistedConfiguration cfg;

        @BeforeEach
        void setUp() {
            Map<String, Object> map = new HashMap<>();
            Consumer<ConfigValue> c = v -> map.put(v.name(), v);
            c.accept(Values.toSimpleValue("a.string", "abc"));
            c.accept(Values.toSimpleValue("a.boolean1", Boolean.TRUE));
            c.accept(Values.toSimpleValue("a.boolean2", Boolean.FALSE));
            c.accept(Values.toSimpleValue("a.int", 123));
            c.accept(Values.toSimpleValue("a.long", 999999999999999999L));
            c.accept(Values.toSimpleValue("a.float", 3.14f));
            c.accept(Values.toSimpleValue("a.double", Math.PI));
            c.accept(Values.toSimpleValue("b.stringList", Arrays.asList("x", "y", "z")));
            this.cfg = new AppConfig(() -> map, writer);
        }

        @Test
        void shouldGetVariable() {
            assertEquals("abc", cfg.root().getString("a.string"));
            assertTrue(cfg.root().getBoolean("a.boolean1"));
            assertFalse(cfg.root().getBoolean("a.boolean2"));
            assertEquals(123, cfg.root().getInt("a.int"));
            assertEquals(999999999999999999L, cfg.root().getLong("a.long"));
            assertEquals(3.14f, cfg.root().getFloat("a.float"));
            assertEquals(Math.PI, cfg.root().getDouble("a.double"));
            assertArrayEquals(new String[]{"x", "y", "z"}, cfg.root().getStringList("b.stringList").toArray());
        }

        @Test
        void shouldKnowVariable() {
            assertTrue(cfg.root().hasVariable("a.string"));
            assertTrue(cfg.root().hasVariable("a.boolean1"));
            assertTrue(cfg.root().hasVariable("a.boolean2"));
            assertTrue(cfg.root().hasVariable("a.int"));
            assertTrue(cfg.root().hasVariable("a.long"));
            assertTrue(cfg.root().hasVariable("a.float"));
            assertTrue(cfg.root().hasVariable("a.double"));
            assertTrue(cfg.root().hasVariable("b.stringList"));
        }

        @Test
        void shouldSetVariable() {
            cfg.root().set("a.string", "xyz");
            assertEquals("xyz", cfg.root().getString("a.string"));

            cfg.root().set("a.boolean1", false);
            assertFalse(cfg.root().getBoolean("a.boolean1"));

            cfg.root().set("a.boolean2", true);
            assertTrue(cfg.root().getBoolean("a.boolean2"));

            cfg.root().set("a.int", 456);
            assertEquals(456, cfg.root().getInt("a.int"));

            cfg.root().set("a.long", Long.MIN_VALUE);
            assertEquals(Long.MIN_VALUE, cfg.root().getLong("a.long"));

            cfg.root().set("a.float", -1f);
            assertEquals(-1f, cfg.root().getFloat("a.float"));

            cfg.root().set("a.double", Double.MAX_VALUE);
            assertEquals(Double.MAX_VALUE, cfg.root().getDouble("a.double"));
        }

        @Test
        void shouldSetNewVariable() {
            cfg.root().set("s1", "c");
            assertEquals("c", cfg.root().getString("s1"));

            cfg.root().set("b1", true);
            assertTrue(cfg.root().getBoolean("b1"));

            cfg.root().set("i1", 5);
            assertEquals(5, cfg.root().getInt("i1"));

            cfg.root().set("l1", Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, cfg.root().getLong("l1"));

            cfg.root().set("f1", -7f);
            assertEquals(-7f, cfg.root().getFloat("f1"));

            cfg.root().set("d1", Double.NaN);
            assertEquals(Double.NaN, cfg.root().getDouble("d1"));
        }

        @Test
        void shouldWireAndSetVariable() {
            State toWire = new State();

            assertEquals(1, toWire.var1());
            assertTrue(toWire.var2());
            assertEquals(3L, toWire.var3());
            assertEquals(4d, toWire.var4());
            assertEquals("5", toWire.var5());

            cfg.wire()
                    .withInt("a.int", toWire::var1, toWire::var1, false)
                    .withBoolean("a.boolean2", toWire::var2, toWire::var2, false)
                    .withLong("a.long", toWire::var3, toWire::var3, false)
                    .withDouble("a.double", toWire::var4, toWire::var4, false)
                    .withString("a.string", toWire::var5, toWire::var5, false)
                    .build();

            assertEquals(123, toWire.var1());
            assertFalse(toWire.var2());
            assertEquals(999999999999999999L, toWire.var3());
            assertEquals(Math.PI, toWire.var4());
            assertEquals("abc", toWire.var5());
        }

        @Test
        void shouldThrowIfTypeMismatch() {
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getBoolean("a.string"));
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getLong("a.string"));
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getDouble("a.string"));
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getStringList("a.string"));
        }

        @Test
        void shouldListAllVariablesAndConstLists() {
            assertArrayEquals(
                    new String[]{
                            "a.boolean1",
                            "a.boolean2",
                            "a.double",
                            "a.float",
                            "a.int",
                            "a.long",
                            "a.string",
                            "b.stringList"
                    },
                    cfg.values()
                            .map(ConfigValue::name)
                            .sorted()
                            .toArray()
            );
        }
    }

    @Test
    void shouldRead() {
        Supplier reader = mock(Supplier.class);
        when(reader.get()).thenReturn(Collections.emptyMap());
        new AppConfig(reader, m -> {
        });
        verify(reader, times(1)).get();
    }

    @Test
    void shouldWrite() {
        new AppConfig(Collections::emptyMap, writer).close();
        verify(writer, times(1)).accept(any(Map.class));
    }

    @Test
    void shouldThrowIfNoVariable() {
        assertThrows(VariableNotFoundException.class,
                () -> new AppConfig(Collections::emptyMap, v -> {
                }).root().getValue("a", StringValue.class)
        );
    }
}

