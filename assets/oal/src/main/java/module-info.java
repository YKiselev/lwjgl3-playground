/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.assets.oal {
    requires org.lwjgl;
    requires org.lwjgl.openal;
    requires org.lwjgl.stb;
    requires transitive ykiselev.wrap;
    requires transitive ykiselev.assets.api;
    requires ykiselev.playground.common;

    exports com.github.ykiselev.openal;
    exports com.github.ykiselev.openal.assets;
    exports com.github.ykiselev.openal.assets.vorbis;
}