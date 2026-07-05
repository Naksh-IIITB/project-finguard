# FinGuard - Smart Expense Tracker

FinGuard is a learning-focused fintech project that simulates a smart expense tracker.
The backend owns the business rules and database, while a separate AI service scores
transactions for unusual or suspicious behavior.

## Services

- `finguard-core-backend`: Spring Boot API, authentication, transaction logic, and database access.
- `finguard-ai-engine`: FastAPI service that scores transactions using an anomaly detection model.
- `finguard-frontend`: React/Vue client for dashboards, forms, and transaction review.
- `docs`: Architecture notes, API contracts, and database design.

## Target Flow

```text
Frontend -> Spring Boot Backend -> PostgreSQL
                         |
                         v
                  FastAPI AI Service -> model.pkl
```

## Learning Path

1. Build the project skeleton.
2. Design the database schema.
3. Create the Spring Boot backend.
4. Add transaction APIs.
5. Add users and authentication.
6. Add spending profile calculations.
7. Create the FastAPI AI service.
8. Connect Spring Boot to FastAPI.
9. Add a real anomaly detection model.
10. Build the frontend.
11. Run everything with Docker Compose.

## Backend Build

From the Spring Boot backend folder:

```bash
cd finguard-core-backend
mvn test
```

This compiles the backend and runs tests. The full server will need PostgreSQL
running once we add database migrations and real entities.
