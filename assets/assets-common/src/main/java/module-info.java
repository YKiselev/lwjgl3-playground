/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 13.04.2019
 */
module ykiselev.playground.assets.common {
    requires typesafe.config;
    requires ykiselev.assets.api;
    requires transitive ykiselev.wrap;
    requires kotlin.stdlib;

    exports com.github.ykiselev.playground.assets.common;
}