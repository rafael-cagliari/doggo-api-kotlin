package com.config.security

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respondText
import java.security.interfaces.RSAPublicKey
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
            }.also {
                println("✅ [DEBUG] Verifier criado com issuer: $issuer")
            }
            validate { credential ->
                println("✅ [DEBUG] Assinatura do JWT foi validada com sucesso")
                println("  - issuer: ${credential.payload.issuer}")
                println("  - audience: ${credential.payload.audience}")
                println("  - subject: ${credential.payload.subject}")
                println("  - expiration: ${credential.payload.expiresAt}")
                println("  - claims: ${credential.payload.claims}")

                if (credential.payload.audience.contains("7l2jo0kh0dmgv5sivu1kut8fha")) {
                    println("✅ [DEBUG] JWT audience válida")
                    JWTPrincipal(credential.payload)
                } else {
                    println("❌ [DEBUG] JWT com audience inválida")
                    null
                }
            }
            challenge { _, _ ->
                call.respondText("invalid or expired token", status = io.ktor.http.HttpStatusCode.Unauthorized)
            }
        }
    }
}
