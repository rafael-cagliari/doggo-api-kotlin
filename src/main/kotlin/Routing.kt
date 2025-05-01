package com

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal

fun Application.configureRouting() {
    routing {

        get("/ping") {
            println("🚀 ping recebido")
            call.respondText("pong")
        }

        get("/") {
            call.respondText("Hello World!")
        }

        authenticate("auth-jwt") {
            get("/secured") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString() ?: "desconhecido"
                call.respondText("Área protegida. Bem-vindo, $username!")
            }
        }
    }
}

