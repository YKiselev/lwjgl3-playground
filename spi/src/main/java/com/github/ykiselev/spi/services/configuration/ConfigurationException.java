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

package com.github.ykiselev.spi.services.configuration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class ConfigurationException extends RuntimeException {

    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     *
     */
    public static final class ConfigNotFoundException extends ConfigurationException {

        public ConfigNotFoundException(String message) {
            super(message);
        }
    }

    /**
     *
     */
    public static final class VariableNotFoundException extends ConfigurationException {

        public VariableNotFoundException(String message) {
            super(message);
        }
    }

    /**
     *
     */
    public static final class VariableAlreadyExistsException extends ConfigurationException {

        public VariableAlreadyExistsException(String message) {
            super(message);
        }
    }

}
