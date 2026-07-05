# Learning Notes - Step 11: Docker Compose Local Stack

## What We Built

Step 11 wires the full local system together with Docker Compose.

The stack contains:

```text
PostgreSQL
Spring Boot backend
FastAPI AI service
React frontend
```

## Why Docker Compose?

Each service needs a different runtime:

```text
PostgreSQL -> database server
Spring Boot -> Java
FastAPI -> Python
React -> Node build, Nginx static server
```

Docker Compose lets us describe all of that in one file.

## Service Map

```text
frontend      http://localhost:3000
backend       http://localhost:8080
ai-engine     http://localhost:8000
postgres      localhost:5432
```

## Internal Networking

Inside Docker Compose, services talk to each other by service name:

```text
core-backend -> postgres:5432
core-backend -> ai-engine:8000
```

That is why the backend uses:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/finguard
FINGUARD_AI_SERVICE_BASE_URL=http://ai-engine:8000
```

The browser still talks to the backend through:

```text
http://localhost:8080
```

## Dockerfiles

Each service has its own Dockerfile:

```text
finguard-core-backend/Dockerfile
finguard-ai-engine/Dockerfile
finguard-frontend/Dockerfile
```

The backend Dockerfile builds a Spring Boot jar.

The AI Dockerfile installs Python dependencies and runs Uvicorn.

The frontend Dockerfile builds React and serves the static files with Nginx.

## CORS

The frontend and backend run on different ports.

The backend now allows requests from:

```text
http://localhost:3000
http://localhost:5173
```

That lets the browser call Spring Boot during local development.

## How to Run

Once Docker is installed:

```bash
docker compose up --build
```

Then open:

```text
http://localhost:3000
```
