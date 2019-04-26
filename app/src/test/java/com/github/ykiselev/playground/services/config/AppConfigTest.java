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

import com.github.ykiselev.spi.services.configuration.ConfigurationException.VariableNotFoundException;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.configuration.values.ConfigValue;
import com.github.ykiselev.spi.services.configuration.values.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@SuppressWarnings("unchecked")
@DisplayName("app config")
public class AppConfigTest {

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

    private FileConfig fileConfig = mock(FileConfig.class);

    @Nested
    @DisplayName("when filled")
    public class WhenFilled {

        PersistedConfiguration cfg = new AppConfig(fileConfig);

        @BeforeEach
        public void setUp() {
            State toWire = new State();
            cfg.wire()
                    .withInt("a.int", toWire::var1, toWire::var1, false)
                    .withBoolean("a.boolean2", toWire::var2, toWire::var2, false)
                    .withLong("a.long", toWire::var3, toWire::var3, false)
                    .withDouble("a.double", toWire::var4, toWire::var4, false)
                    .withString("a.string", toWire::var5, toWire::var5, false)
                    .build();
        }

        @Test
        public void shouldGetVariable() {
            assertEquals("5", cfg.root().getString("a.string"));
            assertTrue(cfg.root().getBoolean("a.boolean2"));
            assertEquals(1, cfg.root().getInt("a.int"));
            assertEquals(3L, cfg.root().getLong("a.long"));
            assertEquals(4d, cfg.root().getDouble("a.double"));
        }

        @Test
        public void shouldKnowVariable() {
            assertTrue(cfg.root().hasVariable("a.string"));
            assertTrue(cfg.root().hasVariable("a.boolean2"));
            assertTrue(cfg.root().hasVariable("a.int"));
            assertTrue(cfg.root().hasVariable("a.long"));
            assertTrue(cfg.root().hasVariable("a.double"));
        }

        @Test
        public void shouldSetVariable() {
            cfg.root().set("a.string", "c");
            assertEquals("c", cfg.root().getString("a.string"));

            cfg.root().set("a.boolean2", false);
            assertFalse(cfg.root().getBoolean("a.boolean2"));

            cfg.root().set("a.int", 5);
            assertEquals(5, cfg.root().getInt("a.int"));

            cfg.root().set("a.long", Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, cfg.root().getLong("a.long"));

            cfg.root().set("a.double", Double.NaN);
            assertEquals(Double.NaN, cfg.root().getDouble("a.double"));
        }

        @Test
        public void shouldThrowIfTypeMismatch() {
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getBoolean("a.string"));
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getLong("a.string"));
            assertThrows(ClassCastException.class, () ->
                    cfg.root().getDouble("a.string"));
        }

        @Test
        public void shouldListAllVariablesAndConstLists() {
            assertArrayEquals(
                    new String[]{
                            "a.boolean2",
                            "a.double",
                            "a.int",
                            "a.long",
                            "a.string"
                    },
                    cfg.values()
                            .map(ConfigValue::name)
                            .sorted()
                            .toArray()
            );
        }
    }

    @Test
    public void shouldRead() {
        new AppConfig(fileConfig);
        verify(fileConfig, times(1)).loadAll("app.conf");
    }

    @Test
    public void shouldWrite() {
        new AppConfig(fileConfig).persist();
        verify(fileConfig, times(1)).persist(eq("app.conf"), any(Map.class));
    }

    @Test
    public void shouldThrowIfNoVariable() {
        assertThrows(VariableNotFoundException.class,
                () -> new AppConfig(fileConfig).root().getValue("a", Values.WiredString.class)
        );
    }
}

