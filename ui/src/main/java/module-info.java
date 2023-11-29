/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.ui {
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires ykiselev.playground.spi;
    requires ykiselev.playground.assets.ogl;
    requires kotlin.stdlib;

    exports com.github.ykiselev.playground.ui;
    exports com.github.ykiselev.playground.ui.elements;
    exports com.github.ykiselev.playground.ui.menus;
    exports com.github.ykiselev.playground.ui.models.checkbox;
    exports com.github.ykiselev.playground.ui.models.slider;
    exports com.github.ykiselev.playground.ui.theme;
}