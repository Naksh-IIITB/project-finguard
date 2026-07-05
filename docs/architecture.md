# Architecture

FinGuard is split into independent services so each part has a clear job.

## Components

```text
React/Vue Frontend
  -> REST API
Spring Boot Backend
  -> PostgreSQL
  -> Internal HTTP call
FastAPI AI Service
  -> Saved model.pkl
```

## Responsibility Split

- The frontend displays data and collects user input.
- The Spring Boot backend validates requests, applies business rules, and stores data.
- PostgreSQL stores users and transactions.
- The FastAPI service receives transaction features and returns risk metrics.

