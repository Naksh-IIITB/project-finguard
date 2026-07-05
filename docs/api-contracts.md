# API Contracts

This file records the request and response shapes before we implement them.

## Create Transaction

```http
POST /api/transactions
```

```json
{
  "userId": 1,
  "amount": 2500.0,
  "category": "Entertainment",
  "merchant": "BookMyShow",
  "transactionTime": "2026-07-05T10:30:00"
}
```

Response:

```json
{
  "id": 1,
  "userId": 1,
  "amount": 2500.0,
  "category": "Entertainment",
  "merchant": "BookMyShow",
  "transactionTime": "2026-07-05T10:30:00",
  "flagged": false,
  "riskScore": 0.0,
  "createdAt": "2026-07-05T10:31:00"
}
```

For Step 4, the frontend sends `userId` directly because authentication has not
been implemented yet. After the auth step, the backend will get the user from
the logged-in session/JWT instead.

## List Transactions for a User

```http
GET /api/transactions?userId=1
```

Response:

```json
[
  {
    "id": 1,
    "userId": 1,
    "amount": 2500.0,
    "category": "Entertainment",
    "merchant": "BookMyShow",
    "transactionTime": "2026-07-05T10:30:00",
    "flagged": false,
    "riskScore": 0.0,
    "createdAt": "2026-07-05T10:31:00"
  }
]
```

## Get One Transaction

```http
GET /api/transactions/1
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
