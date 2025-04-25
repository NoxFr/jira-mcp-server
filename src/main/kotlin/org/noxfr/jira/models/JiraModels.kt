package org.noxfr.jira.models

/**
 * Représente le résultat d'une recherche d'issues dans JIRA
 */

data class SearchResult(
    val startAt: Int = 0,
    val maxResults: Int = 0,
    val total: Int = 0,
    val issues: List<Issue> = emptyList()
)

/**
 * Représente une issue JIRA
 */

data class Issue(
    val id: String,
    val key: String,
    val self: String,
    val fields: Fields
)

/**
 * Représente les champs d'une issue JIRA
 */

data class Fields(
    val summary: String = "",
    val description: Description? = null,
    val status: Status? = null,
    val assignee: User? = null,
    val reporter: User? = null,
    val priority: Priority? = null,
    val issueType: IssueType? = null,
    val project: Project? = null,
    val created: String? = null,
    val updated: String? = null
)

/**
 * Représente une description dans Jira, qui peut être au format Atlassian Document Format
 */

data class Description(
    val content: List<Content> = emptyList(),
    val type: String = ""
)


data class Content(
    val content: List<ContentItem> = emptyList(),
    val type: String = ""
)


data class ContentItem(
    val text: String = "",
    val type: String = ""
)

/**
 * Représente un statut d'issue
 */

data class Status(
    val id: String,
    val name: String,
    val statusCategory: StatusCategory? = null
)

/**
 * Représente une catégorie de statut
 */

data class StatusCategory(
    val id: Int,
    val key: String,
    val name: String
)

/**
 * Représente un utilisateur JIRA
 */

data class User(
    val accountId: String,
    val displayName: String,
    val emailAddress: String? = null,
    val self: String
)

/**
 * Représente une priorité d'issue
 */

data class Priority(
    val id: String,
    val name: String,
    val self: String
)

/**
 * Représente un type d'issue
 */

data class IssueType(
    val id: String,
    val name: String,
    val self: String,
    val description: String? = null,
    val iconUrl: String? = null
)

/**
 * Représente un projet JIRA
 */

data class Project(
    val id: String,
    val key: String,
    val name: String,
    val self: String
)

/**
 * Response structure for transitions
 */
data class TransitionsResponse(
    val transitions: List<Transition>
)

/**
 * Represents a JIRA transition
 */
data class Transition(
    val id: String,
    val name: String,
    val to: TransitionStatus
)

/**
 * Represents a transition status
 */
data class TransitionStatus(
    val id: String,
    val name: String
)

/**
 * Represents a transition request
 */
data class TransitionRequest(
    val transition: TransitionId,
    val update: Update? = null
)

/**
 * Represents a transition ID
 */
data class TransitionId(
    val id: String
)

/**
 * Represents an update operation
 */
data class Update(
    val comment: List<CommentAdd>? = null
)

/**
 * Represents a comment add operation
 */
data class CommentAdd(
    val add: CommentBody
)

/**
 * Represents a comment body
 */
data class CommentBody(
    val body: String
) 