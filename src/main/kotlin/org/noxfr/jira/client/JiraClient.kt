package org.noxfr.jira.client


import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
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

    /**
     * Met à jour une issue existante dans JIRA.
     *
     * @param issueIdOrKey La clé (ex: "DEMO-123") ou l'ID de l'issue à mettre à jour.
     * @param fields Un map contenant les champs à mettre à jour et leurs nouvelles valeurs.
     *               La structure exacte dépend des champs que vous souhaitez modifier.
     *               Exemple: mapOf("summary" to "Nouveau résumé", "priority" to mapOf("id" to "1"))
     * @throws io.ktor.client.plugins.ClientRequestException si la requête échoue (ex: issue non trouvée, champs invalides).
     */
    suspend fun updateIssue(issueIdOrKey: String, fields: Map<String, Any?>) {
        logger.info { "Mise à jour de l'issue: $issueIdOrKey" }

        val response = httpClient.put("${config.baseUrl}${config.apiPath}/issue/$issueIdOrKey") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fields" to fields))
        }

        if (!response.status.isSuccess()) {
            logger.error {"Erreur lors de la mise à jour de l'issue $issueIdOrKey: ${response.status}"}
            throw RuntimeException("Failed to update issue $issueIdOrKey: ${response.status}")
        } else {
            logger.info { "Issue $issueIdOrKey mise à jour avec succès." }
        }
    }

}