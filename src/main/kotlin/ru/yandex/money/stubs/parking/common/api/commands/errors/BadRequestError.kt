package ru.yandex.money.stubs.parking.common.api.commands.errors

class BadRequestError(property: String) : ApplicationError(200, "Illegal property $property")
