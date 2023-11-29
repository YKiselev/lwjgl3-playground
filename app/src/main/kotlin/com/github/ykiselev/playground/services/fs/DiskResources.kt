package com.github.ykiselev.playground.services.fs

import java.io.UncheckedIOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DiskResources(private val paths: Collection<Path>) : ResourceFolder {

    private val writable by lazy {
        paths.any(Files::isWritable)
    }

    constructor(vararg paths: Path) : this(paths.toList())

    private fun find(resource: String, shouldExist: Boolean): Sequence<URL> {
        val preFilter: (Path) -> Boolean = if (shouldExist) {
            { true }
        } else {
            { path -> Files.isWritable(path) }
        }
        val resFilter: (Path) -> Boolean = if (shouldExist) {
            { path -> Files.exists(path) }
        } else {
            { true }
        }
        return paths.asSequence()
            .filter(preFilter)
            .map { p: Path -> p.resolve(resource) }
            .filter(resFilter)
            .map(Path::toUri)
            .map { uri: URI ->
                try {
                    uri.toURL()
                } catch (e: MalformedURLException) {
                    throw UncheckedIOException(e)
                }
            }
    }

    override fun resolve(resource: String, shouldExist: Boolean): URL? =
        find(resource, shouldExist).firstOrNull()

    override fun resolveAll(resource: String): Sequence<URL> =
        find(resource, true)

    override val isWritable: Boolean
        get() = writable
}