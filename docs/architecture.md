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

## Transaction Scoring Flow

```text
Frontend
  -> POST /api/transactions
Spring Boot
  -> validates authenticated user
  -> builds transaction context from spending history
  -> encodes category
  -> POST /predict to FastAPI
FastAPI
  -> returns risk_score and is_anomaly
Spring Boot
  -> saves transaction with risk_score and is_flagged
```

## Responsibility Split

- The frontend displays data and collects user input.
- The Spring Boot backend validates requests, applies business rules, and stores data.
- PostgreSQL stores users and transactions.
- The FastAPI service receives transaction features and returns risk metrics.

## Data Ownership

Spring Boot is the only service that writes to PostgreSQL.

This is important because the backend must protect the rules of the system:

- A transaction must belong to a real user.
- A transaction amount must be valid.
- A transaction should be saved with the AI risk result.
- A user's password must be stored as a hash, never plain text.

The AI service is intentionally stateless for the first version. It receives a
request, calculates risk, returns a response, and does not save the transaction.
That keeps the architecture easier to reason about.
