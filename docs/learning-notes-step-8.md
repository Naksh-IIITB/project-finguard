# Learning Notes - Step 8: Backend to AI Integration

## What We Built

Step 8 connects Spring Boot to FastAPI.

Before this step, Spring Boot saved every transaction with:

```text
riskScore = 0.0
flagged = false
```

After this step, Spring Boot calls the AI service first and saves the returned
risk result.

## New Flow

```text
User creates transaction
  -> TransactionController
  -> TransactionService
  -> UserProfileService builds amountVsAverage
  -> CategoryEncoder converts category to a number
  -> AiRiskClient calls FastAPI /predict
  -> Transaction stores riskScore and flagged
  -> TransactionRepository saves to PostgreSQL
```

## Why We Use WebClient

`WebClient` is Spring's HTTP client.

It lets one backend service call another service:

```text
Spring Boot -> HTTP POST -> FastAPI
```

The frontend does not call FastAPI directly. The backend stays in control.

## AI Request

Spring Boot sends model-friendly features:

```json
{
  "transaction_id": 1,
  "amount": 2500.0,
  "hour_of_day": 10,
  "category_encoded": 2,
  "amount_vs_avg": 12.5
}
```

## AI Response

FastAPI returns:

```json
{
  "transaction_id": 1,
  "risk_score": 0.87,
  "is_anomaly": true
}
```

Spring Boot maps that to:

```text
transaction.riskScore = 0.87
transaction.flagged = true
```

## Temporary Transaction ID

At this stage, the AI request uses a temporary transaction ID because the
transaction has not been saved yet.

Later we can improve this by saving first, scoring second, then updating the row.
For the learning path, this version keeps the flow simple.

## What This Prepares

Step 8 prepares us for real ML.

Right now FastAPI uses mock scoring. Later, FastAPI can load `model.pkl` and keep
the same `/predict` contract.
