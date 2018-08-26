package com.github.ykiselev.lwjgl3.app;

import org.apache.logging.log4j.io.IoBuilder;

import java.io.PrintStream;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class LwjglToLog4j2 implements Supplier<PrintStream> {

    @Override
    public PrintStream get() {
        return IoBuilder.forLogger("LWJGL").buildPrintStream();
    }
}
