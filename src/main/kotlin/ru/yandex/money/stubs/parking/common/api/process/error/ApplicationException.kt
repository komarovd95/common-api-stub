package ru.yandex.money.stubs.parking.common.api.process.error

class ApplicationException(val error: ApplicationError) : Exception(error.toString())
