package ru.yandex.money.stubs.parking.common.api.json

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.commands.errors.BadRequestError
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Json {
    fun toJson(): JSONObject

    fun toJsonString() = toJson().toString()
}

inline fun <reified T> jsonProperty(
        json: JSONObject,
        crossinline onMissingProperty: (String) -> T = { throw BadRequestError(it) }
) = object : ReadOnlyProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>) = when(T::class) {
        String::class -> getProperty(property.name) { json.getString(it) as T }
        Long::class -> getProperty(property.name) { json.getLong(it) as T }
        else -> throw BadRequestError("unknown_type={${T::class}}")
    }

    private fun getProperty(name: String, consumer: (String) -> T) = if (name in json) {
        consumer(name)
    } else {
        onMissingProperty(name)
    }
}
