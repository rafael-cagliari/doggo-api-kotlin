package com.config.security

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respondText
import java.util.concurrent.TimeUnit

private const val issuer = "https://cognito-idp.sa-east-1.amazonaws.com/sa-east-1_4uMvfCPot"
private const val audience = "7l2jo0kh0dmgv5sivu1kut8fha"

fun Application.configureSecurity() {
    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor sample app"
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }
            validate { credential ->
                if (credential.payload.audience.contains(audience)) {
                    JWTPrincipal(credential.payload)
                } else { null }
            }
            challenge { _, _ ->
                call.respondText("invalid or expired token", status = io.ktor.http.HttpStatusCode.Unauthorized)
            }
        }
    }
}
