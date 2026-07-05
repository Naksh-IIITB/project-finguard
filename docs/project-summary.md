# FinGuard Project Summary

FinGuard is a smart expense tracker built as a multi-service fintech-style
application.

## Services

```text
React frontend
Spring Boot backend
PostgreSQL database
FastAPI AI service
Isolation Forest model
```

## Core Flow

```text
User creates transaction
  -> React sends request to Spring Boot
  -> Spring Boot authenticates JWT
  -> Spring Boot calculates spending context
  -> Spring Boot calls FastAPI /predict
  -> FastAPI scores the transaction with model.pkl
  -> Spring Boot saves transaction with riskScore and flagged
  -> React shows the result
```

## Main Learning Milestones

1. Project skeleton
2. Database design
3. Spring Boot setup
4. Transaction API
5. JWT authentication
6. Spending profile metrics
7. FastAPI AI service
8. Backend-to-AI integration
9. Isolation Forest model pipeline
10. React frontend
11. Docker Compose local stack
12. Final polish and run docs

## Useful Commands

Run all tests/builds locally:

```bash
scripts/check-local.sh
```

Run the Docker stack:

```bash
docker compose up --build
```

Run services separately:

```bash
scripts/run-ai.sh
scripts/run-backend.sh
scripts/run-frontend.sh
```
