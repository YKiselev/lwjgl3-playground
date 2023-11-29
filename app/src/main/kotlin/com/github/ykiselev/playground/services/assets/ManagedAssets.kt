package com.github.ykiselev.playground.services.assets

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException
import com.github.ykiselev.common.closeables.Closeables
import com.github.ykiselev.common.lifetime.Ref
import com.github.ykiselev.wrap.Wrap
import org.slf4j.LoggerFactory
import java.nio.channels.ReadableByteChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ManagedAssets(private val delegate: Assets) : Assets, AutoCloseable {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val cache: MutableMap<String, Asset> = ConcurrentHashMap()
    private val loadLock = Any()

    override fun <K, T, C> tryLoad(resource: String, recipe: Recipe<K, T, C>?, assets: Assets): Wrap<T>? {
        while (true) {
            val asset = cache[resource]
            if (asset != null) {
                return asset.value() as Wrap<T>
            }
            synchronized(loadLock) {
                // We can't use  Map#computeIfAbsent because asset can be composite and call this method recursively
                if (!cache.containsKey(resource)) {
                    val wrap = delegate.tryLoad(resource, recipe, assets)
                    if (wrap != null) {
                        cache[resource] = RefAsset(resource, wrap)
                    } else {
                        return null
                    }
                }
            }
        }
    }

    private fun removeFromCache(resource: String, asset: Asset) {
        logger.debug("Removing \"{}\"", resource)
        val removed: Asset?
        synchronized(loadLock) { removed = cache.remove(resource) }
        if (asset !== removed) {
            logger.error("Expected {} but removed {}!", asset, removed)
        }
    }

    override fun <K, T, C> resolve(resource: String?, recipe: Recipe<K, T, C>?): ReadableAsset<T, C>? =
        delegate.resolve(resource, recipe)

    override fun close() {
        Closeables.closeIfNeeded(delegate)
        val errors = cache.entries.stream().map { (key, value) ->
            try {
                Closeables.closeIfNeeded(value)
                null
            } catch (ex: Exception) {
                logger.error("Failed to release asset: {}", key, ex)
                ex.toString()
            }
        }.collect(Collectors.joining("\n"))
        cache.clear()
        check(errors.isEmpty()) { errors }
    }

    override fun open(resource: String): ReadableByteChannel? =
        delegate.open(resource)

    override fun openAll(resource: String): Sequence<ReadableByteChannel> =
        delegate.openAll(resource)

    /**
     *
     */
    private interface Asset {
        fun value(): Wrap<*>
    }

    /**
     *
     */
    private inner class RefAsset(resource: String, value: Wrap<*>) : Asset, AutoCloseable {

        private val ref: Ref<*> = Ref(value.value()) { v: Any? ->
            removeFromCache(resource, this)
            logger.trace("Disposing {}", v)
            value.close()
        }

        override fun value(): Wrap<*> =
            ref.newRef()

        override fun close() {
            ref.close()
        }
    }
}