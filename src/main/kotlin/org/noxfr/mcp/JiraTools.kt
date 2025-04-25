package org.noxfr.mcp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import kotlinx.serialization.json.*
import org.noxfr.jira.client.JiraClient

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

    private val updateIssueTool = RegisteredTool(
        Tool(
            name = "update_issue",
            description = "Update an existing JIRA issue",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("issueKey") {
                        put("type", "string")
                        put("description", "The key of the issue to update")
                    }
                    putJsonObject("fields") {
                        put("type", "object")
                        put("additionalProperties", true)
                        put("description", "Fields to update on the issue")
                    }
                    put("additionalProperties", false)
                },
                required = listOf("issueKey", "fields"),
            )
        )
    ) { request ->
        val issueKey = request.arguments["issueKey"]?.jsonPrimitive?.contentOrNull
        val fields = request.arguments["fields"]?.jsonObject

        if (issueKey == null || fields == null) {
            CallToolResult(
                content = listOf(TextContent("Issue key and fields are required"))
            )
        } else {
            val fieldsMap = fields.entries.associate { (key, value) ->
                key to when (value) {
                    is JsonPrimitive -> if (value.isString) value.content else value.toString()
                    else -> value.toString()
                }
            }
            CallToolResult(
                content = listOf(
                    TextContent(
                        runCatching {
                            jiraClient.updateIssue(issueKey, fieldsMap) // Assuming this method exists
                            "Issue $issueKey updated successfully."
                        }.onFailure {
                            logger.error(it) { "Error updating issue $issueKey" }
                        }.getOrDefault("Failed to update issue $issueKey.")
                    )
                )
            )
        }
    }

    fun tools() = listOf(searchIssuesTool, getIssueTool, updateIssueTool)
}

fun main() {
    buildJsonObject {
        putJsonObject("issueKey") {
            put("type", "string")
            put("description", "The key of the issue to update")
        }
        putJsonObject("fields") {
            put("type", "array")
            putJsonObject("items") {
                put("type", "string")
                put("description", "Field to update on the issue")
            }
        }
    }.also { println(it.toString()) }
}