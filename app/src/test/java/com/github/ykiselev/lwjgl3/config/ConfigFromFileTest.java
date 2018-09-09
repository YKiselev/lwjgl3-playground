package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.configuration.values.BooleanValue;
import com.github.ykiselev.services.configuration.values.DoubleValue;
import com.github.ykiselev.services.configuration.values.LongValue;
import com.github.ykiselev.services.configuration.values.StringValue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.Channels;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ConfigFromFileTest {

    @Test
    void shouldRead() {
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
        assertArrayEquals(new String[]{"a=b", "c=d"}, ((ConstantList) map.get("services")).list().toArray());
    }
}