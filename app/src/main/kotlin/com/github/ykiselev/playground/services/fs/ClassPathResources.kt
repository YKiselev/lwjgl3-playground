package com.github.ykiselev.playground.services.fs

import java.net.URL

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ClassPathResources(private val loader: ClassLoader) : ResourceFolder {

    override fun resolve(resource: String, shouldExist: Boolean): URL? =
        sequenceOf(resource)
            .mapNotNull { name -> loader.getResource(name) }
            .firstOrNull()


    override fun resolveAll(resource: String): Sequence<URL> =
        loader.getResources(resource).asSequence()

}