package org.noxfr.jira.config

/**
 * Configuration du client JIRA contenant les informations de connexion
 */
data class JiraClientConfig(
    /**
     * URL de base de l'instance JIRA (ex: https://your-domain.atlassian.net)
     */
    val baseUrl: String,

    /**
     * Email du compte utilisateur pour l'authentification
     */
    val email: String,

    /**
     * Clé API (API Token) générée dans les paramètres de votre compte Atlassian
     */
    val apiToken: String,

    /**
     * Chemin de l'API REST (par défaut API v3)
     */
    val apiPath: String = "/rest/api/3",

    /**
     * Timeout des requêtes en millisecondes
     */
    val requestTimeoutMillis: Long = 30000
)