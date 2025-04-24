package org.noxfr.mcp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import org.noxfr.jira.client.JiraClient
import kotlinx.serialization.json.*

class JiraTools(private val jiraClient: JiraClient) {
    private val logger = KotlinLogging.logger { }

    private val searchIssuesTool = RegisteredTool(
        Tool(
            name = "search_issues",
            description = "Search JIRA issues using JQL",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("searchString") {
                        put("type", "string")
                        put("description", "JQL search string")
                        put("required", true)
                    }
                },
                required = listOf("searchString"),
            )
        )
    ) { request ->
        val contentOrNull = request.arguments["searchString"]?.jsonPrimitive?.contentOrNull
        if (contentOrNull == null) {
            CallToolResult(
                content = listOf(TextContent("Search string is required"))
            )
        } else {
            CallToolResult(
                content = jiraClient.searchIssues(contentOrNull).issues
                    .takeIf { it.isNotEmpty() }
                    ?.map { TextContent(jacksonObjectMapper().writeValueAsString(it)) }
                    ?: listOf(TextContent("No issues found"))
            )
        }
    }

    private val getIssueTool = RegisteredTool(
        Tool(
            name = "get_issue",
            description = "Get detailed information about a specific JIRA issue including comments",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("issueId") {
                        put("type", "string")
                        put("description", "The ID or key of the JIRA issue")
                        put("required", true)
                    }
                },
                required = listOf("issueId"),
            )
        )
    ) { request ->
        val issueId = request.arguments["issueId"]?.jsonPrimitive?.contentOrNull
        if (issueId == null) {
            CallToolResult(
                content = listOf(TextContent("Issue ID is required"))
            )
        } else {
            CallToolResult(
                content = listOf(
                    TextContent(
                        runCatching {
                            jacksonObjectMapper().writeValueAsString(jiraClient.getIssueDetails(issueId))
                        }.onFailure {
                            logger.error { "Error while retrieving issue details: ${it.message}" }
                        }.getOrDefault("Cannot get issue details")
                    )
                )
            )
        }
    }

    fun tools() = listOf(searchIssuesTool, getIssueTool)
} 