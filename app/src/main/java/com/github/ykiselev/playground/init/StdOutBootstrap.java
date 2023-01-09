package com.github.ykiselev.playground.init;

import org.apache.logging.log4j.io.IoBuilder;

import java.io.PrintStream;

public final class StdOutBootstrap implements AutoCloseable {

    public StdOutBootstrap() {
        final PrintStream std = IoBuilder.forLogger("STD").buildPrintStream();
        System.setOut(std);
        System.setErr(std);
    }

    @Override
    public void close() {
        // no-op
    }
}
