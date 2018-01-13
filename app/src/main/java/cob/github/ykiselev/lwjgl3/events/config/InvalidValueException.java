package cob.github.ykiselev.lwjgl3.events.config;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class InvalidValueException extends RuntimeException {

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
