package com.github.ykiselev.common.memory.pool

import com.github.ykiselev.common.memory.Allocator
import java.util.*

class ObjectPoolFrame<T> internal constructor(val owner: ObjectPool<T>) : AutoCloseable, Allocator<T> {

    private val pool: MutableList<T> = ArrayList()

    private var index = 0

    fun size(): Int {
        return pool.size
    }

    fun index(): Int {
        return index
    }

    override fun allocate(): T {
        if (index == pool.size) {
            pool.add(owner.newInstance())
        }
        return pool[index++]
    }

    override fun close() {
        index = 0
        owner.pop(this)
    }
}
