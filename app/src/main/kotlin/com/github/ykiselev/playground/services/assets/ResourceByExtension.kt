package com.github.ykiselev.playground.services.assets

import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.ReadableAssets
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ResourceByExtension(private val map: Map<String, ReadableAsset<*, *>>) : ReadableAssets {

    override fun <K, T, C> resolve(resource: String?, recipe: Recipe<K, T, C>?): ReadableAsset<T, C>? =
        map[extension(resource)] as? ReadableAsset<T, C>

    private fun extension(resource: String?): String? =
        resource?.let {
            val i = it.lastIndexOf('.')
            return if (i >= 0) {
                it.substring(i + 1)
            } else null
        }
}