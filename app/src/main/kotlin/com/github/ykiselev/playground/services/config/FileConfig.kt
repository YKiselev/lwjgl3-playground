package com.github.ykiselev.playground.services.config

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
interface FileConfig {
    fun getValue(name: String): Any?
    fun persist(name: String, config: Map<String, Any>)
    fun load(name: String)
    fun loadAll(name: String)
}