package org.noxfr

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.noxfr.jira.config.JiraClientConfig
import org.noxfr.mcp.JiraMcpServer

val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    val command = args.firstOrNull() ?: "--sse-server-ktor"
    val jiraClientConfig = JiraClientConfig(
        baseUrl = System.getenv("JIRA_URL"),
        email = System.getenv("JIRA_EMAIL"),
        apiToken = System.getenv("JIRA_PAT"),
    )
    when (command) {
        "--stdio" -> runMcpServerUsingStdio(jiraClientConfig)
        "--sse-server-ktor" -> runSseMcpServerUsingKtorPlugin(jiraClientConfig)
        else -> {
            System.err.println("Unknown command: $command")
        }
    }
}
private fun runSseMcpServerUsingKtorPlugin(jiraClientConfig: JiraClientConfig) {
    logger.info { "Running MCP server using Ktor MCP plugin" }
    embeddedServer(CIO, host = "0.0.0.0", port = 3001) {
        mcp { JiraMcpServer(jiraClientConfig) }
    }.start(wait = true)
}

private fun runMcpServerUsingStdio(jiraClientConfig: JiraClientConfig) {
    logger.info { "Running MCP server using stdio" }
    val server = JiraMcpServer(jiraClientConfig)
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