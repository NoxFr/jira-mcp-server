package org.noxfr.jira.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

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
        jackson { configureObjectMapper() }
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

    // Configuration par défaut pour les requêtes
    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}

private fun ObjectMapper.configureObjectMapper() = apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}