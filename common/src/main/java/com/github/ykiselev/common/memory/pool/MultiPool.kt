package com.github.ykiselev.common.memory.pool

class MultiPool<V>(
    private val frameDecorator: (MultiPool<V>.Frame) -> V,
    private val factory: (Class<*>) -> Any
) {

    data class PoolInfo(val size: Int, val index: Int)

    private inner class Pool<T> {

        private val pool = mutableListOf<T>()

        private var index = 0

        fun reset() {
            index = 0
        }

        private fun newInstance(clazz: Class<T>): T = factory(clazz) as T

        fun allocate(clazz: Class<T>): T {
            if (index == pool.size) {
                pool.add(newInstance(clazz))
            }
            return pool[index++]
        }

        fun toInfo(): PoolInfo =
            PoolInfo(size = pool.size, index = index)
    }

    inner class Frame : AutoCloseable {

        private val map = mutableMapOf<Class<*>, Pool<*>>()

        private fun <T> pool(clazz: Class<T>): Pool<T> =
            map.computeIfAbsent(clazz) {
                Pool<T>()
            } as Pool<T>

        fun <T> allocate(clazz: Class<T>): T =
            pool(clazz).allocate(clazz)

        override fun close() {
            map.forEach { (_, pool) -> pool.reset() }
        }

        /**
         * For debug purposes only (dumping stats, etc)
         */
        fun visit(block: (Map<Class<*>, PoolInfo>) -> Unit) {
            block(
                map.mapValues { (_, v) ->
                    v.toInfo()
                }
            )
        }
    }

    private val frames = mutableListOf<V>()

    private var index = 0

    fun push(): V {
        if (index == frames.size) {
            frames.add(frameDecorator(Frame()))
        }
        return frames[index++]
    }

    fun pop(frame: V) {
        check(frames[index - 1] == frame) { "Wrong frame to pop: $frame" }
        index--
    }

    /**
     * For debug purposes only (dumping stats, etc)
     */
    fun visit(block: (List<V>) -> Unit) {
        block(frames.toList())
    }
}