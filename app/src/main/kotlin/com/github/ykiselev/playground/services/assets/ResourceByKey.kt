package com.github.ykiselev.playground.services.assets

import com.github.ykiselev.assets.ReadableAsset
import com.github.ykiselev.assets.ReadableAssets
import com.github.ykiselev.assets.Recipe
import com.github.ykiselev.assets.ResourceException

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ResourceByKey<Key>(private val map: Map<Key, ReadableAsset<*, *>>) : ReadableAssets {

    @Throws(ResourceException::class)
    override fun <K, T, C> resolve(resource: String?, recipe: Recipe<K, T, C>?): ReadableAsset<T, C>? =
        recipe?.let {
            map[recipe.key() as Key] as? ReadableAsset<T, C>
        }
}