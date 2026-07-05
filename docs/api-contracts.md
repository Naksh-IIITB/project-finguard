# API Contracts

This file records the request and response shapes before we implement them.

## Register

```http
POST /api/auth/register
```

```json
{
  "username": "demo",
  "email": "demo@finguard.local",
  "password": "password123"
}
```

Response:

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "demo",
  "email": "demo@finguard.local"
}
```

## Login

```http
POST /api/auth/login
```

```json
{
  "usernameOrEmail": "demo",
  "password": "password123"
}
```

Response:

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "demo",
  "email": "demo@finguard.local"
}
```

## Create Transaction

```http
POST /api/transactions
Authorization: Bearer <token>
```

```json
{
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

The frontend no longer sends `userId`. The backend gets the user from the JWT.

## List My Transactions

```http
GET /api/transactions
Authorization: Bearer <token>
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
Authorization: Bearer <token>
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
