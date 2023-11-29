package com.github.ykiselev.playground.services.config

import com.github.ykiselev.common.cow.CopyOnModify
import com.github.ykiselev.spi.services.FileSystem
import com.github.ykiselev.spi.services.configuration.Config
import com.github.ykiselev.spi.services.configuration.ConfigurationException
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration
import com.github.ykiselev.spi.services.configuration.values.ConfigValue
import java.util.stream.Stream

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppConfig internal constructor(private val fileConfig: FileConfig) : PersistedConfiguration, AutoCloseable {

    private val config: CopyOnModify<Map<String, ConfigValue>> = CopyOnModify(emptyMap())
    private val varFilter = { obj: Any? -> obj is ConfigValue }
    private val root: Config = object : Config {

        override fun <V : ConfigValue?> getValue(
            path: String,
            clazz: Class<V>
        ): V {
            return clazz.cast(getRawValue(path)) ?: throw ConfigurationException.VariableNotFoundException(path)
        }

        override fun hasVariable(path: String): Boolean {
            return varFilter(getRawValue(path))
        }
    }

    init {
        loadAll("app.conf")
    }

    constructor(fileSystem: FileSystem) : this(AppFileConfig(fileSystem))

    override fun values(): Stream<ConfigValue> {
        return config.value()
            .values
            .stream()
            .filter(varFilter)
    }

    override fun root(): Config {
        return root
    }

    private fun getRawValue(path: String): Any? {
        return config.value()[path]
    }

    @Throws(ConfigurationException.VariableAlreadyExistsException::class)
    override fun wire(values: Collection<ConfigValue>): AutoCloseable {
        val toWire = toMap(values)
        require(toWire.isNotEmpty()) { "Nothing to wire!" }
        config.modify { before: Map<String, ConfigValue>? ->
            val after: MutableMap<String, ConfigValue> = HashMap(before)
            toWire.forEach { (k: String, newValue: ConfigValue) ->
                if (after.containsKey(k)) {
                    throw ConfigurationException.VariableAlreadyExistsException(k)
                }
                after[k] = newValue
                val rawValue = fileConfig.getValue(k)
                if (rawValue != null) {
                    newValue.fromObject(rawValue)
                }
            }
            after
        }
        val keysToRemove = mutableSetOf(*toWire.keys.toTypedArray<String>())
        return AutoCloseable {
            config.modify { before: Map<String, ConfigValue> ->
                val result = before.toMutableMap()
                result.keys.removeAll(keysToRemove)
                result
            }
        }
    }

    private fun toMap(values: Collection<ConfigValue>): Map<String, ConfigValue> =
        values.associateBy { it.name }

    override fun persist(name: String) {
        fileConfig.persist(
            name,
            config.value()
                .values
                .associateBy(ConfigValue::name, ConfigValue::boxed)
        )
    }

    private fun applyFileValues() {
        config.modify { before ->
            val after = before.toMutableMap()
            after.forEach { (k: String, v: ConfigValue) ->
                fileConfig.getValue(k)?.also {
                    v.fromObject(it)
                }
            }
            after
        }
    }

    override fun load(name: String) {
        fileConfig.load(name)
        applyFileValues()
    }

    override fun loadAll(name: String) {
        fileConfig.loadAll(name)
        applyFileValues()
    }

    override fun close() {
        // todo
        //writer.accept(config.value());
    }
}