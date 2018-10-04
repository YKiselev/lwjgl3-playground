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

package com.github.ykiselev.opengl.shaders;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.shaders.uniforms.UniformInfo;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ProgramObject extends Bindable, AutoCloseable {

    /**
     * @param uniform the uniform variable name
     * @return the uniform variable location
     * @throws ProgramException if uniform variable not found
     */
    int uniformLocation(String uniform) throws ProgramException.UniformVariableNotFoundException;

    /**
     * @param attribute the attribute name
     * @return the attribute location
     * @throws ProgramException if attribute not found
     */
    int attributeLocation(String attribute) throws ProgramException.AttributeNotFoundException;

    /**
     * @param uniform the name of the uniform variable
     * @return the new instance of uniform variable
     * @throws ProgramException if active uniform variable cannot be found
     */
    UniformVariable lookup(String uniform) throws ProgramException.UniformVariableNotFoundException;

    /**
     * @param location the location of active uniform variable to describe
     * @return the variable information
     */
    UniformInfo describe(int location);

    /**
     * @param variable the active uniform variable to describe
     * @return the variable information
     */
    default UniformInfo describe(UniformVariable variable) {
        return describe(variable.location());
    }
}
