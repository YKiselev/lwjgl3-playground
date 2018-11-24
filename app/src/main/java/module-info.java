/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.app {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.openal;
    requires org.lwjgl.opengl;
    requires slf4j.api;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires config;
    requires org.apache.logging.log4j.iostreams;
    requires jsr305;

    requires ykiselev.wrap;
    requires ykiselev.assets.api;
    requires ykiselev.playground.common;
    requires ykiselev.playground.spi;
    requires ykiselev.playground.assets.ogl;
    requires ykiselev.playground.assets.oal;
    requires ykiselev.playground.ui;
}