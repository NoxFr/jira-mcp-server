package org.noxfr.jira.models

import kotlinx.serialization.Serializable

/**
 * Représente le résultat d'une recherche d'issues dans JIRA
 */
@Serializable
data class SearchResult(
    val startAt: Int = 0,
    val maxResults: Int = 0,
    val total: Int = 0,
    val issues: List<Issue> = emptyList()
)

/**
 * Représente une issue JIRA
 */
@Serializable
data class Issue(
    val id: String,
    val key: String,
    val self: String,
    val fields: Fields
)

/**
 * Représente les champs d'une issue JIRA
 */
@Serializable
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
@Serializable
data class Description(
    val content: List<Content> = emptyList(),
    val type: String = ""
)

@Serializable
data class Content(
    val content: List<ContentItem> = emptyList(),
    val type: String = ""
)

@Serializable
data class ContentItem(
    val text: String = "",
    val type: String = ""
)

/**
 * Représente un statut d'issue
 */
@Serializable
data class Status(
    val id: String,
    val name: String,
    val statusCategory: StatusCategory? = null
)

/**
 * Représente une catégorie de statut
 */
@Serializable
data class StatusCategory(
    val id: Int,
    val key: String,
    val name: String
)

/**
 * Représente un utilisateur JIRA
 */
@Serializable
data class User(
    val accountId: String,
    val displayName: String,
    val emailAddress: String? = null,
    val self: String
)

/**
 * Représente une priorité d'issue
 */
@Serializable
data class Priority(
    val id: String,
    val name: String,
    val self: String
)

/**
 * Représente un type d'issue
 */
@Serializable
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
@Serializable
data class Project(
    val id: String,
    val key: String,
    val name: String,
    val self: String
)

/**
 * Response structure for transitions
 */
@Serializable
data class TransitionsResponse(
    val transitions: List<Transition>
)

/**
 * Represents a JIRA transition
 */
@Serializable
data class Transition(
    val id: String,
    val name: String,
    val to: TransitionStatus
)

/**
 * Represents a transition status
 */
@Serializable
data class TransitionStatus(
    val id: String,
    val name: String
)

/**
 * Represents a transition request
 */
@Serializable
data class TransitionRequest(
    val transition: TransitionId,
    val update: Update? = null
)

/**
 * Represents a transition ID
 */
@Serializable
data class TransitionId(
    val id: String
)

/**
 * Represents an update operation
 */
@Serializable
data class Update(
    val comment: List<CommentAdd>? = null
)

/**
 * Represents a comment add operation
 */
@Serializable
data class CommentAdd(
    val add: CommentBody
)

/**
 * Represents a comment body
 */
@Serializable
data class CommentBody(
    val body: String
) 