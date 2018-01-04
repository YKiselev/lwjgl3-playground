package com.github.ykiselev.openal;

import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetString;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Errors {

    public static void assertNoAlErrors() {
        final int err = alGetError();
        if (err != AL_NO_ERROR) {
            throw new RuntimeException(alGetString(err));
        }
    }
}
