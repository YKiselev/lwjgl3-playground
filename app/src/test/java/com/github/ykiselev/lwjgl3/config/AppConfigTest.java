package com.github.ykiselev.lwjgl3.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppConfigTest {

    @Test
    void shouldRead() {
        Config config = ConfigFactory.parseResources("test.conf");

        final Map<String, Object> map = config.root()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().unwrapped()
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
}