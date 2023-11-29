package com.github.ykiselev.playground.services.fs

import java.net.URL

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
interface ResourceFolder {

    fun resolve(resource: String): URL? {
        return resolve(resource, true)
    }

    fun resolve(resource: String, shouldExist: Boolean): URL?
    fun resolveAll(resource: String): Sequence<URL>
    val isWritable: Boolean
        get() = false
}