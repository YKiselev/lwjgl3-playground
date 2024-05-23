package com.github.ykiselev.opengl.pools

import com.github.ykiselev.common.memory.pool.MultiPool
import com.github.ykiselev.opengl.matrices.MathAllocator
import com.github.ykiselev.opengl.matrices.Matrix
import com.github.ykiselev.opengl.matrices.Vector3f
import com.github.ykiselev.opengl.matrices.Vector4f
import org.lwjgl.system.MemoryUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R> math(block: MathAllocator.() -> R): R =
    MathPools.push().use {
        it.block()
    }

fun printMathPoolInfo(consumer: (String) -> Unit) {
    MathPools.printInfo(consumer)
}

private class MathPools(val frame: MultiPool<MathPools>.Frame) : MathAllocator, AutoCloseable {

    override fun <T> instance(clazz: Class<T>): T =
        frame.allocate(clazz)

    override fun close() {
        frame.close()
        TLS.get().pop(this)
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(MathPools::class.java)

        private val factories = mapOf(
            Vector3f::class.java to { Vector3f() },
            Vector4f::class.java to { Vector4f() },
            Matrix::class.java to { Matrix(MemoryUtil.memAllocFloat(16)) }
        )

        private val TLS = ThreadLocal.withInitial {
            MultiPool({ frame ->
                MathPools(frame)
            }, { clazz ->
                factories[clazz]?.invoke() ?: throw IllegalStateException("Unable to create instance of $clazz")
            })
        }

        fun push(): MathPools =
            TLS.get().push()

        fun printInfo(consumer: (String) -> Unit) {
            TLS.get().visit { framesDecorators ->
                framesDecorators.forEach {
                    consumer("Frame:")
                    it.frame.visit { map ->
                        map.forEach { (k, v) ->
                            consumer("\t$k: $v")
                        }
                    }
                }
            }
        }
    }
}