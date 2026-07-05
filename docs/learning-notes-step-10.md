# Learning Notes - Step 10: React Frontend

## What We Built

Step 10 adds the first user interface for FinGuard.

The frontend lives in:

```text
finguard-frontend
```

It is a React app built with Vite.

## Why React?

React lets us build the dashboard as reusable pieces:

```text
AuthScreen
Metric
TransactionForm
RiskPanel
TransactionTable
```

Each component owns one part of the screen.

## Frontend Responsibility

The frontend does not own security, database writes, or AI scoring.

Its job is:

```text
show forms
send API requests
store JWT token locally
render dashboard data
show risk results
```

Spring Boot still owns the real rules.

## Auth Flow

The user logs in or registers through:

```http
POST /api/auth/login
POST /api/auth/register
```

The backend returns:

```text
JWT token
username
email
```

The frontend stores that token in local storage and sends it on protected
requests:

```http
Authorization: Bearer <token>
```

## Dashboard Flow

After login, the frontend loads:

```http
GET /api/profile/spending
GET /api/transactions
```

Those responses fill the metric tiles, risk panel, and transaction table.

## Transaction Flow

When a user creates a transaction, the frontend sends:

```http
POST /api/transactions
```

Spring Boot then:

```text
checks the JWT
builds spending context
calls FastAPI
saves the risk result
returns the transaction data
```

The frontend only shows the final result.

## Important Boundary

The frontend never calls FastAPI directly.

That is intentional. The backend protects the system by controlling database
writes and AI orchestration.
