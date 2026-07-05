# API Contracts

This file records the request and response shapes before we implement them.

## Create Transaction

```http
POST /api/transactions
```

```json
{
  "amount": 2500.0,
  "category": "Entertainment",
  "merchant": "BookMyShow",
  "timestamp": "2026-07-05T10:30:00"
}
```

## AI Risk Prediction

```http
POST /predict
```

```json
{
  "transaction_id": 1024,
  "amount": 2500.0,
  "hour_of_day": 10,
  "category": "Entertainment",
  "amount_vs_avg": 3.4
}
```

```json
{
  "transaction_id": 1024,
  "risk_score": 0.87,
  "is_anomaly": true
}
```

