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

import com.github.ykiselev.services.FileSystem;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.Channels;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@Deprecated
class ConfigFromFileTest {

    @Test
    void shouldRead() {
/*
        FileSystem fs = Mockito.mock(FileSystem.class);
        ConfigFromFile fromFile = new ConfigFromFile(fs);

        Mockito.when(fs.openAll(anyString()))
                .thenReturn(
                        Stream.of(
                                Channels.newChannel(getClass().getResourceAsStream("/test.conf"))
                        ));

        Map<String, Object> map = fromFile.get();
        assertEquals(10, ((LongValue) map.get("sound.volume")).value());
        assertEquals(999999999999999999L, ((LongValue) map.get("sound.key")).value());
        assertTrue(((BooleanValue) map.get("sound.extension.enabled")).value());
        assertEquals(15.5, ((DoubleValue) map.get("sound.extension.spatial-sound.base")).value());
        assertEquals("b1", ((StringValue) map.get("mouse.buttons.left")).value());
        assertArrayEquals(new String[]{"a=b", "c=d"}, ((ConstantList) map.get("services")).toList().toArray());
        */
    }
}