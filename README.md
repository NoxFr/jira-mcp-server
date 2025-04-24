# Jira MCP Server

Ce projet est un serveur qui agit comme un pont entre Jira et le protocole MCP (Model Context Protocol). Il permet de synchroniser et d'interagir avec les données Jira via une interface MCP.

## Technologies utilisées

- **Kotlin** : Langage de programmation principal
- **Ktor** : Framework web pour la création d'API REST
- **Gradle** : Système de build
- **Docker** : Containerisation de l'application
- **MCP (Model Context Protocol)** : Protocole de communication utilisé pour l'interopérabilité

## Prérequis

- Java 21 ou supérieur
- Docker et Docker Compose
- Un compte Jira avec les permissions appropriées

## Configuration

Le projet nécessite les variables d'environnement suivantes :

- `JIRA_URL` : L'URL de votre instance Jira
- `JIRA_EMAIL` : L'email associé à votre compte Jira
- `JIRA_API_TOKEN` : Le token d'API Jira

## Lancement en local via Docker Compose

Voici un exemple de configuration docker-compose.yml :

```yaml
services:
  jira-mcp-server:
    build: .
    ports:
      - "3001:3001"
    environment:
      - JIRA_URL=https://votre-instance.atlassian.net
      - JIRA_EMAIL=votre.email@exemple.com
      - JIRA_PAT=votre_token_api_jira
```

## Construction et exécution

Pour construire et exécuter le projet :

```bash
# Construire l'image Docker
docker-compose build

# Démarrer le service
docker-compose up
```

Le serveur sera accessible sur le port 3001.

## Exemples d'utilisations avec un Assistant IA

Voici des exemples d'interactions avec le MCP via un assistant IA (comme Claude Desktop ou Cursor) :

- Corrige moi tous les tickets du projet XXX concernant des erreurs de libellés et passe les en relecture
- Propose moi une implémentation pour le ticket XXX
etc.