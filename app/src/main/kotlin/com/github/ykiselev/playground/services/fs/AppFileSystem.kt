package com.github.ykiselev.playground.services.fs

import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.spi.services.FileSystem
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.UncheckedIOException
import java.net.URISyntaxException
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.file.*

/**
 * @param folders the read-only resource folders sorted by priority.
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppFileSystem(vararg folders: ResourceFolder) : FileSystem {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val folders: List<ResourceFolder> = folders.toList()

    private fun open(resource: URL, vararg options: OpenOption): FileChannel {
        val path = toPath(resource)
        logger.debug("Opening file channel {}...", path)
        ensureParentFoldersExists(path)
        return try {
            FileChannel.open(path, *options)
        } catch (e: IOException) {
            throw UncheckedIOException("Unable to open $path", e)
        }
    }

    private fun ensureParentFoldersExists(path: Path) {
        val parent = path.parent
        if (parent != null) {
            try {
                Files.createDirectories(parent)
            } catch (e: IOException) {
                throw UncheckedIOException(e)
            }
        }
    }

    private fun resolveResourceFolder(writable: Boolean): ResourceFolder? {
        val predicate: (ResourceFolder) -> Boolean =
            if (writable) ResourceFolder::isWritable else {
                { true }
            }
        return folders.asSequence()
            .filter(predicate)
            .firstOrNull()
    }

    override fun open(name: String, vararg options: OpenOption): FileChannel {
        val hasWrite = hasOption(options, StandardOpenOption.WRITE)
        return resolveResourceFolder(hasWrite)
            ?.resolve(name, !hasWrite)
            ?.let {
                open(it, *options)
            } ?: throw IllegalStateException("Unable to open archive!")
    }

    override fun mapArchive(archiveName: String, create: Boolean, readOnly: Boolean): FileSystem =
        resolveResourceFolder(!readOnly)
            ?.resolve(archiveName, readOnly)
            ?.let {
                ArchiveFileSystem.create(toPath(it), create)
            } ?: throw IllegalStateException("Unable to map archive: not found!")


    @Throws(ResourceException::class)
    override fun open(resource: String): ReadableByteChannel? =
        folders.asSequence()
            .mapNotNull { f: ResourceFolder -> f.resolve(resource) }
            .map { channel(resource, it) }
            .firstOrNull()

    @Throws(ResourceException::class)
    override fun openAll(resource: String): Sequence<ReadableByteChannel> =
        folders.asSequence()
            .flatMap { it.resolveAll(resource) }
            .map { channel(resource, it) }

    private fun channel(resource: String, url: URL): ReadableByteChannel {
        logger.debug("Resource {} resolved to {}", resource, url)
        return Channels.newChannel(
            url.openStream()
        )
    }

    override fun close() {
        // no-op for now
    }

    companion object {

        private fun toPath(url: URL): Path {
            return try {
                Paths.get(url.toURI()).toAbsolutePath()
            } catch (e: URISyntaxException) {
                throw ResourceException("Unable to convert to path: $url", e)
            }
        }

        private fun hasOption(options: Array<out OpenOption>, option: OpenOption): Boolean {
            for (opt in options) {
                if (option == opt) {
                    return true
                }
            }
            return false
        }
    }
}