package com.github.ykiselev.services.configuration;

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
}
