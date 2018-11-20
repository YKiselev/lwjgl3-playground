/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.base {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires slf4j.api;

    requires ykiselev.wrap;
    requires ykiselev.assets.api;
    requires ykiselev.playground.common;
    requires ykiselev.playground.spi;
    requires ykiselev.playground.assets.ogl;

    exports com.github.ykiselev.base.game;
}