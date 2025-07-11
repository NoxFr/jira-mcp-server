package org.noxfr.mcp

import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import org.noxfr.jira.client.JiraClient

fun mcpServer(
    jiraClient: JiraClient,
) = Server(
    Implementation(
        name = "mcp-kotlin-jira-server",
        version = "0.1.0"
    ),
    ServerOptions(
        capabilities = ServerCapabilities(
            tools = ServerCapabilities.Tools(listChanged = false),
        )
    )
).apply {
    addTools(JiraTools(jiraClient).tools())
}