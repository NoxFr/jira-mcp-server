package org.noxfr

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.noxfr.jira.client.JiraClient
import org.noxfr.mcp.mcpServer
import org.slf4j.event.Level

val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    val command = args.firstOrNull() ?: "--stdio"
    
    // Initialisation de Koin
    startKoin {
        modules(appModule)
    }
    
    when (command) {
        "--stdio" -> runMcpServerUsingStdio()
        "--sse-server-ktor" -> runSseMcpServerUsingKtorPlugin()
        else -> {
            System.err.println("Unknown command: $command")
        }
    }
}

private fun runSseMcpServerUsingKtorPlugin() {
    logger.info { "Running MCP server using Ktor MCP plugin" }
    embeddedServer(Netty, host = "0.0.0.0", port = 3001) {
        install(CallLogging){
            level = Level.DEBUG
        }

        install(Koin) {
            modules(appModule)
        }
        install(ContentNegotiation) {
            json()
        }
        mcp {
            val jiraClient by inject<JiraClient>()
            mcpServer(jiraClient)
        }
    }.start(wait = true)
}

private fun runMcpServerUsingStdio() {
    logger.info { "Running MCP server using stdio" }
    val jiraClient = GlobalContext.get().get<JiraClient>()
    val server = mcpServer(jiraClient)
    val transport = StdioServerTransport(
        inputStream = System.`in`.asSource().buffered(),
        outputStream = System.out.asSink().buffered()
    )
    runBlocking {
        server.connect(transport)
        val done = Job()
        server.onClose {
            done.complete()
        }
        done.join()
        logger.info { "Server closed" }
    }
}