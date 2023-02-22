/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.common {
    requires jdk.unsupported;
    requires ykiselev.wrap;
    requires ykiselev.assets.api;
    requires org.lwjgl;
    requires org.apache.commons.io;
    requires config;
    requires static java.management;

    exports com.github.ykiselev.common.caching;
    exports com.github.ykiselev.common.circular;
    exports com.github.ykiselev.common.closeables;
    exports com.github.ykiselev.common.collections;
    exports com.github.ykiselev.common;
    exports com.github.ykiselev.common.conversion;
    exports com.github.ykiselev.common.cow;
    exports com.github.ykiselev.common.fps;
    exports com.github.ykiselev.common.io;
    exports com.github.ykiselev.common.iterators;
    exports com.github.ykiselev.common.lazy;
    exports com.github.ykiselev.common.lifetime;
    exports com.github.ykiselev.common.memory;
    exports com.github.ykiselev.common.memory.scrap;
    exports com.github.ykiselev.common.memory.pool;
    exports com.github.ykiselev.common.pools;
    exports com.github.ykiselev.common.test;
    exports com.github.ykiselev.common.tree;
    exports com.github.ykiselev.common.trigger;
    exports com.github.ykiselev.common.math;
    exports com.github.ykiselev.common.recursion;
}
