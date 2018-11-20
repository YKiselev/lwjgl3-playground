/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
module ykiselev.playground.assets.ogl {
    requires org.lwjgl;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires commons.lang3;
    requires slf4j.api;
    requires sprite.font.lib;
    requires config;
    requires ykiselev.wrap;
    requires ykiselev.assets.api;
    requires ykiselev.playground.common;

    exports com.github.ykiselev.opengl;
    exports com.github.ykiselev.opengl.assets.formats;
    exports com.github.ykiselev.opengl.assets.formats.obj;
    exports com.github.ykiselev.opengl.buffers;
    exports com.github.ykiselev.opengl.matrices;
    exports com.github.ykiselev.opengl.models;
    exports com.github.ykiselev.opengl.shaders;
    exports com.github.ykiselev.opengl.shaders.uniforms;
    exports com.github.ykiselev.opengl.sprites;
    exports com.github.ykiselev.opengl.text;
    exports com.github.ykiselev.opengl.textures;
    exports com.github.ykiselev.opengl.vbo;
    exports com.github.ykiselev.opengl.vertices;
}