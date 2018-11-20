/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.spi {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires config;
    requires ykiselev.assets.api;
    requires ykiselev.playground.common;
    requires ykiselev.playground.assets.ogl;

    exports com.github.ykiselev.api;
    exports com.github.ykiselev.components;
    exports com.github.ykiselev.services;
    exports com.github.ykiselev.services.commands;
    exports com.github.ykiselev.services.configuration;
    exports com.github.ykiselev.services.configuration.values;
    exports com.github.ykiselev.services.events;
    exports com.github.ykiselev.services.layers;
    exports com.github.ykiselev.services.schedule;
    exports com.github.ykiselev.spi;
    exports com.github.ykiselev.window;
}