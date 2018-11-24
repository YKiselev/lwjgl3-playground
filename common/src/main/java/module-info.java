/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.common {
    requires jdk.unsupported;
    requires ykiselev.wrap;
    requires ykiselev.assets.api;
    requires org.lwjgl;
    requires commons.io;
    requires static java.management;

    exports com.github.ykiselev.caching;
    exports com.github.ykiselev.circular;
    exports com.github.ykiselev.closeables;
    exports com.github.ykiselev.collections;
    exports com.github.ykiselev.common;
    exports com.github.ykiselev.conversion;
    exports com.github.ykiselev.cow;
    exports com.github.ykiselev.fps;
    exports com.github.ykiselev.io;
    exports com.github.ykiselev.iterators;
    exports com.github.ykiselev.lazy;
    exports com.github.ykiselev.lifetime;
    exports com.github.ykiselev.memory;
    exports com.github.ykiselev.memory.scrap;
    exports com.github.ykiselev.test;
    exports com.github.ykiselev.tree;
    exports com.github.ykiselev.trigger;
}
