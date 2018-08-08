package ru.yandex.money.stubs.parking.common.api.config

import java.util.Properties

class ApplicationConfig(properties: Properties) {
    val host: String by properties
    val port: String by properties
    val databasePath: String by properties
    val defaultLogin: String by properties
    val defaultPassword: String by properties

    companion object {
        val DefaultConig = ApplicationConfig(
            Properties().also {
                it.setProperty("host", "0.0.0.0")
                it.setProperty("port", "8080")
                it.setProperty("databasePath", "./parking-stub.db")
                it.setProperty("defaultLogin", "yandex")
                it.setProperty("defaultPassword", "yandex")
            }
        )
    }
}
