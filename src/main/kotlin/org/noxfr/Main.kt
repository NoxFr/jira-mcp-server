package org.noxfr

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import org.noxfr.jira.config.JiraClientConfig
import org.noxfr.mcp.JiraMcpServer

val logger = KotlinLogging.logger { }

fun main() {
    logger.info { "Running MCP server using Ktor MCP plugin" }
    embeddedServer(CIO, host = "0.0.0.0", port = 3001) {
        val jiraClientConfig = JiraClientConfig(
            baseUrl = System.getenv("JIRA_URL"),
            email = System.getenv("JIRA_EMAIL"),
            apiToken = System.getenv("JIRA_PAT"),
        )
        mcp { JiraMcpServer(jiraClientConfig) }
    }.start(wait = true)
}