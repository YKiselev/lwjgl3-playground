package com.github.ykiselev.playground.services.fs

import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.common.closeables.Closeables
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.file.*
import java.util.*

/**
 * Wrapper for [java.nio.file.FileSystem] created from zip archive
 */
class ArchiveFileSystem(private val fs: FileSystem) : com.github.ykiselev.spi.services.FileSystem {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun close() {
        Closeables.close(fs)
    }

    @Throws(ResourceException::class)
    override fun open(resource: String): ReadableByteChannel? =
        try {
            FileChannel.open(fs.getPath(resource), StandardOpenOption.READ)
        } catch (e: IOException) {
            logger.error("Failed to open file!", e)
            null
        }


    @Throws(ResourceException::class)
    override fun openAll(resource: String): Sequence<ReadableByteChannel> =
        open(resource)?.let { sequenceOf(it) } ?: emptySequence()

    override fun open(name: String, vararg options: OpenOption): FileChannel =
        FileChannel.open(fs.getPath(name), *options)

    override fun mapArchive(
        archiveName: String,
        create: Boolean,
        readOnly: Boolean
    ): com.github.ykiselev.spi.services.FileSystem =
        create(fs.getPath(archiveName), create)

    companion object {

        @JvmStatic
        fun create(path: Path, create: Boolean): com.github.ykiselev.spi.services.FileSystem =
            path.let {
                Files.createDirectories(it.parent)
                ArchiveFileSystem(
                    FileSystems.newFileSystem(
                        URI.create("jar:file:$path"),
                        mapOf("create" to create.toString())
                    )
                )
            }
    }
}