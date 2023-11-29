package com.github.ykiselev.playground.init

import org.apache.logging.log4j.io.IoBuilder

fun initStdOut() {
    with(IoBuilder.forLogger("STD").buildPrintStream()) {
        System.setOut(this)
        System.setErr(this)
    }
}
