package ru.yandex.money.stubs.parking.common.api.json

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.commands.errors.BadRequestError
import java.math.BigDecimal
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSuperclassOf

abstract class JsonDeserializable(private val json: JSONObject) {

    protected constructor(jsonString: String) : this(JSONObject(jsonString))

    private val propertiesCache = ConcurrentHashMap<String, Any>()

    override fun toString() = "JsonDeserializable(json=$json)"

    protected fun <T> jsonProperty(
            onComplexType: (JSONObject) -> T,
            onMissingProperty: (String) -> T = { throw BadRequestError(it) }
    ): ReadOnlyProperty<Any, T> {
        return JsonProperty(json, propertiesCache, onComplexType, onMissingProperty)
    }

    protected fun <T> jsonProperty(onMissingProperty: (String) -> T = { throw BadRequestError(it) }) = jsonProperty(
            onComplexType = { throw BadRequestError(it.toString()) },
            onMissingProperty = onMissingProperty
    )


    private class JsonProperty<T>(
            private val json: JSONObject,
            private val cache: MutableMap<String, Any>,
            private val onComplexType: (JSONObject) -> T,
            private val onMissingProperty: (String) -> T
    ) : ReadOnlyProperty<Any, T> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val name = property.name
            if (name in cache) {
                return cache[name] as T
            }

            val propertyClass = property.returnType.classifier as KClass<*>

            return when {
                propertyClass == String::class -> getProperty(name) { json.getString(it) as T }
                propertyClass == Long::class -> getProperty(name) { json.getLong(it) as T }
                propertyClass == BigDecimal::class -> getProperty(name) { BigDecimal(json.getString(it)) as T }
                propertyClass == Duration::class -> getProperty(name) { Duration.parse(json.getString(it)) as T }
                propertyClass == JSONObject::class -> getProperty(name) { json.getJSONObject(it) as T }
                JsonDeserializable::class.isSuperclassOf(propertyClass) -> getProperty(name) { onComplexType(json.getJSONObject(it)) }
                else -> throw BadRequestError("unknown_type={$propertyClass}")
            }
        }

        private fun getProperty(name: String, consumer: (String) -> T) = if (name in json) {
            val value = consumer(name)
            cache[name] = value as Any
            value
        } else {
            onMissingProperty(name)
        }
    }
}