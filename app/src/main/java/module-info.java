import com.github.ykiselev.spi.GameFactory;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.app {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.openal;
    requires org.lwjgl.opengl;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires typesafe.config;
    requires org.apache.logging.log4j.iostreams;
    requires commons.lang3;
    requires kotlin.stdlib;

    requires static org.jetbrains.annotations;

    requires ykiselev.wrap;
    requires ykiselev.assets.api;
    requires ykiselev.playground.common;
    requires ykiselev.playground.spi;
    requires ykiselev.playground.assets.ogl;
    requires ykiselev.playground.assets.oal;
    requires ykiselev.playground.ui;

    opens com.github.ykiselev.playground.services.console.appender;

    exports com.github.ykiselev.playground;

    uses GameFactory;
}