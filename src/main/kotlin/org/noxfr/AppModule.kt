package org.noxfr

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.noxfr.jira.client.JiraClient
import org.noxfr.jira.config.JiraClientConfig

/**
 * Module Koin pour l'injection de dépendance de l'application
 */
val appModule = module {
    single<JiraClientConfig> {
        JiraClientConfig(
            baseUrl = System.getenv("JIRA_URL"),
            email = System.getenv("JIRA_EMAIL"),
            apiToken = System.getenv("JIRA_PAT"),
        )
    }
    singleOf(::JiraClient)
} 