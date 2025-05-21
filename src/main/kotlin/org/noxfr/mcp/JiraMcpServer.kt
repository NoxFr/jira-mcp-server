package org.noxfr.mcp

import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import org.noxfr.jira.client.JiraClient
import org.noxfr.jira.config.JiraClientConfig

class JiraMcpServer(
    jiraClientConfig: JiraClientConfig,
) : Server(
    Implementation(
        name = "mcp-kotlin-jira-server",
        version = "0.1.0"
    ),
    ServerOptions(
        capabilities = ServerCapabilities(
            prompts = null,
            resources = null,
            tools = ServerCapabilities.Tools(listChanged = null),
        )
    )
) {
    private val logger = KotlinLogging.logger { }
    private val jiraClient = JiraClient(jiraClientConfig)
    private val jiraTools = JiraTools(jiraClient)

    init {
        logger.info { "JIRA server initialized" }
        addTools(jiraTools.tools())
    }
}