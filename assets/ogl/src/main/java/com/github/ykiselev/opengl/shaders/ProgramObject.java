package com.github.ykiselev.opengl.shaders;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ProgramObject extends Bindable, AutoCloseable {

    int uniformLocation(String uniform) throws ProgramException;

    int attributeLocation(String attribute) throws ProgramException;

    UniformVariable lookup(String uniform) throws ProgramException;
}
