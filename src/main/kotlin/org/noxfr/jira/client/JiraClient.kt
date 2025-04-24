package org.noxfr.jira.client


import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.noxfr.jira.config.JiraClientConfig
import org.noxfr.jira.config.jiraHttpClient
import org.noxfr.jira.models.Issue
import org.noxfr.jira.models.SearchResult

private val logger = KotlinLogging.logger {}

/**
 * Client pour l'API JIRA permettant de rechercher et récupérer des issues
 */
class JiraClient(private val config: JiraClientConfig) {

    private val httpClient = jiraHttpClient(config)

    /**
     * Recherche des issues selon une requête JQL (Jira Query Language)
     *
     * @param jql Requête JQL (ex: "project = DEMO AND status = Open")
     * @param startAt Index de départ pour la pagination
     * @param maxResults Nombre maximum de résultats à retourner
     * @param fields Liste des champs à inclure dans les résultats
     * @return SearchResult contenant les issues correspondant à la requête
     */
    suspend fun searchIssues(
        jql: String,
        startAt: Int = 0,
        maxResults: Int = 50,
        fields: List<String> = listOf("summary", "status", "assignee", "reporter", "description")
    ): SearchResult {
        logger.info { "Recherche d'issues avec JQL: $jql" }

        val response = httpClient.post("${config.baseUrl}${config.apiPath}/search") {
            setBody(
                mapOf(
                    "jql" to jql,
                    "startAt" to startAt,
                    "maxResults" to maxResults,
                    "fields" to fields
                )
            )
        }

        return response.body()
    }

    /**
     * Récupère les détails d'une issue spécifique par sa clé ou son ID
     *
     * @param issueIdOrKey Clé (ex: "DEMO-123") ou ID de l'issue
     * @param fields Liste des champs à inclure dans les résultats
     * @return Issue contenant les détails de l'issue
     */
    suspend fun getIssueDetails(
        issueIdOrKey: String,
        fields: List<String> = listOf(
            "summary",
            "status",
            "assignee",
            "reporter",
            "description",
            "priority",
            "issuetype",
            "project",
            "created",
            "updated"
        )
    ): Issue {
        logger.info { "Récupération des détails de l'issue: $issueIdOrKey" }

        val response = httpClient.get("${config.baseUrl}${config.apiPath}/issue/$issueIdOrKey") {
            parameter("fields", fields.joinToString(","))
        }

        return response.body()
    }

}