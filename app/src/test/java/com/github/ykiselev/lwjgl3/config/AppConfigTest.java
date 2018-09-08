package com.github.ykiselev.lwjgl3.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * todo - write real test
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppConfigTest {

    @Test
    void shouldRead() {
        Config config = ConfigFactory.parseResources("test.conf");

        final Map<String, Object> map = config.root()
                .entrySet()
                .stream()
                .flatMap(e -> denormalize(e.getKey(), e.getValue().unwrapped()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        System.out.println(map);

        Config cfg = ConfigFactory.parseMap(map);
        final String rendered = cfg.root()
                .render(
                        ConfigRenderOptions.defaults()
                                .setFormatted(true)
                                .setOriginComments(false)
                                .setJson(false)
                );
        System.out.println("Rendered config from map: " + rendered);
    }

    private Stream<Map.Entry<String, Object>> denormalize(String key, Object value) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            return map.entrySet()
                    .stream()
                    .flatMap(e -> denormalize(key + "." + e.getKey(), e.getValue()));
        } else {
            return Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>(
                            key,
                            value
                    )
            );
        }
    }

    @Test
    void shouldWrite() {
        Map<String, Object> map = new HashMap<>();
        map.put("a.b.c.value", 1);
        map.put("a.b.d", 2);
        map.put("a.b.e", 1);
        map.put("a.b.c.services", Arrays.asList("a=b", "c=d"));

        System.out.println(map);

        Config cfg = ConfigFactory.parseMap(map);
        final String rendered = cfg.root()
                .render(
                        ConfigRenderOptions.defaults()
                                .setFormatted(true)
                                .setOriginComments(false)
                                .setJson(false)
                );
        System.out.println("Rendered config from map: " + rendered);
    }
}

