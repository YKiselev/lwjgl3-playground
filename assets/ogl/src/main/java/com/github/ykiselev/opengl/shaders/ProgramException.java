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

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class ProgramException extends RuntimeException {

    public ProgramException(String message) {
        super(message);
    }

    public ProgramException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgramException(Throwable cause) {
        super(cause);
    }

    public ProgramException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static final class AttributeNotFoundException extends ProgramException {

        public AttributeNotFoundException(String name) {
            super("Attribute not found: " + name
                    + ".\nThis may be caused by compiler optimization, check if attribute is actually used in code!");
        }
    }

    public static final class UniformVariableNotFoundException extends ProgramException {

        public UniformVariableNotFoundException(String name) {
            super("Uniform variable not found: " + name +
                    ".\nThis may be caused by compiler optimization, check if variable is actually used in code!");
        }
    }
}
