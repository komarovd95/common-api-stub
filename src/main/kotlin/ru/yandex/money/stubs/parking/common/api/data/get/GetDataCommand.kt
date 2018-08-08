package ru.yandex.money.stubs.parking.common.api.data.get

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonCommand
import ru.yandex.money.stubs.parking.common.api.service.data.DataService

class GetDataCommand internal constructor(private val dataService: DataService) : Command<Unit, GetDataResponse> {

    override fun invoke(request: Unit): GetDataResponse {
        log.info("Getting all data from DB.")
        val data = dataService.data()
        return GetDataResponse(data)
    }

    companion object {
        private val log = LoggerFactory.getLogger(GetDataCommand::class.java)
    }
}

fun getDataCommand(dataService: DataService) = CommonCommand("get-data", GetDataCommand(dataService)) {}
