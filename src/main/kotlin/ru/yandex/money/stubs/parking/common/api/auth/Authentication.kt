package ru.yandex.money.stubs.parking.common.api.auth

import io.ktor.http.ContentType
import io.ktor.pipeline.ContextDsl
import io.ktor.request.header
import io.ktor.routing.*
import ru.yandex.money.stubs.parking.common.api.process.token.TokenRegistry
import java.util.regex.Pattern

lateinit var commonTokenRegistry: TokenRegistry

class AuthorizationSelector(private val tokenRegistry: TokenRegistry) :
        RouteSelector(RouteSelectorEvaluation.qualityConstant) {

    companion object {
        private val PATTERN = Pattern.compile("^Token ([a-fA-F0-9]{32})$")
    }

    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        if (context.segments.last() == "token") {
            return RouteSelectorEvaluation.Failed
        }

        val header = context.call.request.header("Authorization") ?: throw AuthorizationException()

        val matcher = PATTERN.matcher(header)
        if (!matcher.matches()) {
            throw AuthorizationException()
        }

        val token = matcher.group(1)
        if (token !in tokenRegistry) {
            throw AuthorizationException()
        }
        return RouteSelectorEvaluation(succeeded = true, quality = 1.0)
    }
}

@ContextDsl
fun Route.authenticated(build: Route.() -> Unit): Route {
    val selector = AuthorizationSelector(commonTokenRegistry)
    return createChild(selector).apply(build)
}