/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.opengl.shaders.uniforms;

import java.nio.FloatBuffer;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class UniformVariable {

    private final int location;

    private final String name;

    public int location() {
        return location;
    }

    public String name() {
        return name;
    }

    public UniformVariable(int location, String name) {
        this.location = location;
        this.name = requireNonNull(name);
    }

    public void matrix4(boolean transpose, FloatBuffer matrix) {
        glUniformMatrix4fv(location, transpose, matrix);
    }

    /**
     * Specifies the value of a single vec4 uniform variable or a vec4 uniform variable array.
     *
     * @param buffer the buffer with vec4 components.
     */
    public void vector4(FloatBuffer buffer) {
        glUniform4fv(location, buffer);
    }

    /**
     * Specifies the value of a single vec3 uniform variable or a vec3 uniform variable array.
     *
     * @param buffer the buffer with vec3 components.
     */
    public void vector3(FloatBuffer buffer) {
        glUniform3fv(location, buffer);
    }

    /**
     * Specifies the value of a single vec2 uniform variable or a vec2 uniform variable array.
     *
     * @param buffer the buffer with vec2 components.
     */
    public void vector2(FloatBuffer buffer) {
        glUniform2fv(location, buffer);
    }

    public void value(int value) {
        glUniform1i(location, value);
    }

}
