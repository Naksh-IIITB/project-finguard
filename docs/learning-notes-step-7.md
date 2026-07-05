# Learning Notes - Step 7: FastAPI AI Service Setup

## What We Built

Step 7 creates the Python AI microservice:

```text
finguard-ai-engine
```

This service exposes:

```http
GET /health
POST /predict
```

It does not train or load a real machine learning model yet. For now, it uses a
deterministic scoring function so the backend can integrate with a stable API.

## Why a Separate AI Service?

Spring Boot is good for business rules, authentication, and database ownership.

Python is strong for data science and machine learning.

So we split the responsibilities:

```text
Spring Boot -> owns users, transactions, auth, database
FastAPI -> calculates transaction risk
```

## Request Contract

The AI service expects transaction features:

```json
{
  "transaction_id": 1024,
  "amount": 2500.0,
  "hour_of_day": 10,
  "category_encoded": 3,
  "amount_vs_avg": 3.4
}
```

These are not raw frontend fields. They are model-friendly numbers.

## Response Contract

The AI service returns:

```json
{
  "transaction_id": 1024,
  "risk_score": 0.55,
  "is_anomaly": false
}
```

Spring Boot will later save `risk_score` and `is_flagged` on the transaction.

## Mock Risk Scoring

For now, risk is based on simple rules:

```text
high amount_vs_avg -> higher risk
large amount -> higher risk
late night hour -> slightly higher risk
```

This is not the final AI model. It is a temporary scoring engine.

## Why Mock First?

Mocking first lets us test the service contract before adding ML complexity.

The development order is:

```text
API contract -> backend integration -> real model
```

That makes bugs easier to isolate.
