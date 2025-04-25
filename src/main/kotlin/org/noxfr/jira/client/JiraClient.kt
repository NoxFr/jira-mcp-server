package org.noxfr.jira.client

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.noxfr.jira.config.JiraClientConfig
import org.noxfr.jira.config.jiraHttpClient
import org.noxfr.jira.models.*

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

    /**
     * Gets the available transitions for a JIRA issue
     *
     * @param issueIdOrKey The key (ex: "DEMO-123") or ID of the issue
     * @return A list of available transitions
     */
    suspend fun getTransitions(issueIdOrKey: String): List<Transition> {
        logger.info { "Getting transitions for issue: $issueIdOrKey" }

        val response = httpClient.get("${config.baseUrl}${config.apiPath}/issue/$issueIdOrKey/transitions") {
            contentType(ContentType.Application.Json)
        }

        if (!response.status.isSuccess()) {
            logger.error { "Error getting transitions for issue $issueIdOrKey: ${response.status}" }
            throw RuntimeException("Failed to get transitions for issue $issueIdOrKey: ${response.status}")
        }

        val transitionsResponse = response.body<TransitionsResponse>()
        return transitionsResponse.transitions
    }

    /**
     * Performs a transition on a JIRA issue
     *
     * @param issueIdOrKey The key (ex: "DEMO-123") or ID of the issue
     * @param transitionId The ID of the transition to perform
     * @param comment Optional comment to add with the transition
     */
    suspend fun transitionIssue(issueIdOrKey: String, transitionId: String, comment: String? = null) {
        logger.info { "Transition de l'issue: $issueIdOrKey vers le statut: $transitionId" }

        val request = TransitionRequest(
            transition = TransitionId(transitionId),
            update = if (comment != null) {
                Update(
                    comment = listOf(
                        CommentAdd(
                            add = CommentBody(comment)
                        )
                    )
                )
            } else null
        )

        val response = httpClient.post("${config.baseUrl}${config.apiPath}/issue/$issueIdOrKey/transitions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            logger.error { "Error transitioning issue $issueIdOrKey: ${response.status}" }
            throw RuntimeException("Failed to transition issue $issueIdOrKey: ${response.status}")
        } else {
            logger.info { "Issue $issueIdOrKey transitioned successfully" }
        }
    }

    /**
     * Récupère la liste des utilisateurs JIRA
     *
     * @param query Chaîne de recherche pour filtrer les utilisateurs
     * @param maxResults Nombre maximum de résultats à retourner
     * @return Liste des utilisateurs correspondant à la recherche
     */
    suspend fun getUsers(query: String = "", maxResults: Int = 50): List<User> {
        logger.info { "Recherche d'utilisateurs avec la requête: $query" }

        val response = httpClient.get("${config.baseUrl}${config.apiPath}/user/search") {
            parameter("query", query)
            parameter("maxResults", maxResults)
        }

        return response.body()
    }

    /**
     * Assigns a JIRA issue to a user.
     *
     * @param issueIdOrKey The key (ex: "DEMO-123") or ID of the issue to assign.
     * @param accountId The account ID of the user to assign the issue to. Use "-1" to assign to the default assignee or null/empty string to unassign.
     * @throws io.ktor.client.plugins.ClientRequestException if the request fails (ex: issue not found, user not found).
     */
    suspend fun assignIssue(issueIdOrKey: String, accountId: String?) {
        logger.info { "Assigning issue $issueIdOrKey to user account ID: ${accountId ?: "Unassigned"}" }

        val requestBody = mapOf("accountId" to accountId)

        val response = httpClient.put("${config.baseUrl}${config.apiPath}/issue/$issueIdOrKey/assignee") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            logger.error { "Error assigning issue $issueIdOrKey: ${response.status}" }
            throw RuntimeException("Failed to assign issue $issueIdOrKey: ${response.status}")
        } else {
            logger.info { "Issue $issueIdOrKey assigned successfully to account ID: ${accountId ?: "Unassigned"}." }
        }
    }
}