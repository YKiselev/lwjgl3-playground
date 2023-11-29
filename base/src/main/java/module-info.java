import com.github.ykiselev.base.game.spi.BaseGameFactory;
import com.github.ykiselev.spi.GameFactory;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.base {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires slf4j.api;

    requires ykiselev.playground.common;
    requires ykiselev.playground.spi;
    requires ykiselev.playground.assets.ogl;
    requires kotlin.stdlib;

    provides GameFactory with BaseGameFactory;
}