package com.github.ykiselev.common.memory

interface Allocator<T> {

    fun allocate(): T
}