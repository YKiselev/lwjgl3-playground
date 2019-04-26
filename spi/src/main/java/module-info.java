/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.spi {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires config;
    requires transitive ykiselev.assets.api;
    requires ykiselev.playground.common;
    requires ykiselev.playground.assets.ogl;

    exports com.github.ykiselev.spi.api;
    exports com.github.ykiselev.spi.components;
    exports com.github.ykiselev.spi.services;
    exports com.github.ykiselev.spi.services.commands;
    exports com.github.ykiselev.spi.services.configuration;
    exports com.github.ykiselev.spi.services.configuration.values;
    exports com.github.ykiselev.spi.services.events;
    exports com.github.ykiselev.spi.services.layers;
    exports com.github.ykiselev.spi.services.schedule;
    exports com.github.ykiselev.spi;
    exports com.github.ykiselev.spi.window;
}