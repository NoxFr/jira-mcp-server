# Jira MCP Server

This project is a server that acts as a bridge between Jira and the MCP (Model Context Protocol). It allows synchronization and interaction with Jira data through an MCP interface.

## Technologies Used

- **Kotlin**: Main programming language
- **Ktor**: Web framework for creating REST API
- **Gradle**: Build system
- **Docker**: Application containerization
- **MCP (Model Context Protocol)**: Communication protocol used for interoperability

## Docker Packages

This project provides two Docker images that are built and pushed to the GitHub Container Registry:
- **jira-mcp-stdio**: An image configured for stdio mode communication.
- **jira-mcp-sse**: An image configured for SSE (Server-Sent Events) mode communication.

**Note**: These images are automatically built and pushed on updates to the main branch.

You can pull these images from the GitHub Container Registry using the following commands:
```bash
docker pull ghcr.io/noxfr/jira-mcp-stdio:latest
docker pull ghcr.io/noxfr/jira-mcp-sse:latest
```

## Development prerequisites

- Java 21 or higher
- Docker and Docker Compose
- A Jira account with appropriate permissions

## Configuration

The project requires the following environment variables:

- `JIRA_URL`: The URL of your Jira instance
- `JIRA_EMAIL`: The email associated with your Jira account
- `JIRA_API_TOKEN`: The Jira API token

2 MCP modes: SSE or stdio

## Running Locally via Docker Compose

Here is an example configuration for docker-compose.yml:

```yaml
services:
  jira-mcp-server:
    build: .
    ports:
      - "3001:3001"
    environment:
      - JIRA_URL=https://your-instance.atlassian.net
      - JIRA_EMAIL=your.email@example.com
      - JIRA_PAT=your_jira_api_token
```

## Building and Running

To build and run the project:

```bash
# Build the Docker image
docker-compose build

# Start the service
docker-compose up
```

The server will be accessible on port 3001.

## Adding MCP

Configuration within the IDE

*HTTP*

```json
{
  "mcpServers": {
    "mcp-kotlin-jira-server": {
      "url": "http://127.0.0.1:3001/sse"
    }
  }
}
```

*Stdio*
```json
{
  "mcpServers": {
    "mcp-kotlin-jira-stdio": {
      "command": "docker",
      "args": [
        "run",
        "--rm",
        "-i",
        "-e",
        "JIRA_URL",
        "-e",
        "JIRA_EMAIL",
        "-e",
        "JIRA_PAT",
        "docker.io/library/jira-mcp-server-jira-mcp-server"
      ],
      "env": {
        "JIRA_URL": "TBD",
        "JIRA_EMAIL": "TBD",
        "JIRA_PAT": "TBD"
      }
    }
  }
}
```
## Usage Examples with an AI Assistant

Here are examples of interactions with MCP via an AI assistant (like Claude Desktop or Cursor):

- Fix all tickets in project XXX concerning label errors and set them to review
- Suggest an implementation for ticket XXX
etc.

## Available Tools

The server exposes the following tools via MCP:

- `search_issues`: Search JIRA issues using JQL.
- `get_issue`: Get detailed information about a specific JIRA issue including comments.
- `update_issue`: Update an existing JIRA issue.
- `get_transitions`: Get available status transitions for a JIRA issue.
- `transition_issue`: Change the status of a JIRA issue by performing a transition.
- `get_users`: Search for JIRA users.
- `assign_issue`: Assign a JIRA issue to a user. 