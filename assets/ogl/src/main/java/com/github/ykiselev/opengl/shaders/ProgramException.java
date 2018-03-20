package com.github.ykiselev.opengl.shaders;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ProgramException extends RuntimeException {

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
}
