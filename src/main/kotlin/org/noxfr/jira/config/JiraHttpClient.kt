package org.noxfr.jira.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun jiraHttpClient(config: JiraClientConfig) = HttpClient(CIO) {
    expectSuccess = true
    // Configuration du timeout
    install(HttpTimeout) {
        requestTimeoutMillis = config.requestTimeoutMillis
    }

    // Configuration du logging HTTP
    install(Logging) {
        level = LogLevel.ALL
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true

            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
        })
    }

    install(Auth) {
        basic {
            sendWithoutRequest { true }
            credentials {
                BasicAuthCredentials(
                    username = config.email,
                    password = config.apiToken
                )
            }
        }
    }

    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}