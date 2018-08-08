package ru.yandex.money.stubs.parking.common.api.data.change

import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonCommand
import ru.yandex.money.stubs.parking.common.api.service.data.DataService

class ChangeDataCommand(private val dataService: DataService) : Command<ChangeDataRequest, ChangeDataResponse> {
    override fun invoke(request: ChangeDataRequest): ChangeDataResponse {
        dataService.changeData(request.method, request.collectionName, request.data)
        return ChangeDataResponse()
    }
}

fun changeDataCommand(dataService: DataService) =
    CommonCommand("change-data", ChangeDataCommand(dataService)) { ChangeDataRequest(it) }
