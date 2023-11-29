/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.assets.api {
    requires slf4j.api;
    requires transitive ykiselev.wrap;
    requires kotlin.stdlib;

    exports com.github.ykiselev.assets;
}