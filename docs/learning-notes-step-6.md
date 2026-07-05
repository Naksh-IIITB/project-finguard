# Learning Notes - Step 6: Spending Profile Logic

## What We Built

Step 6 teaches the backend how to summarize a user's transaction history.

This matters because AI needs context.

The same transaction can mean different things for different users:

```text
User A average spend: 200
New transaction: 2500
amountVsAverage = 12.5000

User B average spend: 3000
New transaction: 2500
amountVsAverage = 0.8333
```

For User A, the transaction is much larger than normal. For User B, it is
ordinary.

## New Endpoints

```http
GET /api/profile/spending
GET /api/profile/transaction-context?amount=2500.00&category=Entertainment
```

Both endpoints are protected. The frontend sends a JWT token, and the backend
uses the authenticated user ID from that token.

## Spending Profile

The spending profile returns dashboard-style metrics:

```text
transactionCount
averageTransactionAmount
currentMonthSpend
month
```

This is useful for a future dashboard.

## Transaction Context

The transaction context endpoint answers:

```text
How unusual does this amount look compared with this user's history?
```

It returns:

```text
averageTransactionAmount
categoryAverageAmount
amountVsAverage
amountVsCategoryAverage
```

`amountVsAverage` is calculated like this:

```text
new transaction amount / user's average transaction amount
```

`amountVsCategoryAverage` is calculated like this:

```text
new transaction amount / user's average amount in that category
```

## Why We Query Aggregates in the Repository

The repository asks PostgreSQL for aggregates like:

```sql
avg(amount)
sum(amount)
count(*)
```

This is better than loading every transaction into Java and calculating there.

Databases are built to calculate aggregates efficiently.

## No-History Case

If the user has no transaction history, the average is zero.

In that case, the ratio returns:

```text
0.0000
```

We avoid dividing by zero.

## How This Prepares the AI Step

Later, when a user creates a transaction, the backend can build this context:

```text
amount
hourOfDay
category
amountVsAverage
amountVsCategoryAverage
```

Then Spring Boot can send those features to FastAPI.

Step 6 does not call FastAPI yet. It prepares the data that FastAPI will need.
