package com.github.ykiselev.wrap

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
abstract class Wrap<T>(private val value: T) : AutoCloseable {

    fun value(): T =
        value

    abstract override fun close()

    override fun toString(): String =
        "Wrap{$value}"
}