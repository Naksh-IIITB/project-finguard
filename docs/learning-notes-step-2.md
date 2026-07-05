# Learning Notes - Step 2: Database Design

## What We Designed

FinGuard starts with two tables:

```text
users
transactions
```

The relationship is:

```text
one user -> many transactions
```

This is represented by storing `user_id` inside the `transactions` table.

## Primary Key

A primary key uniquely identifies one row in a table.

Example:

```text
users.id = 1
```

No other user can have the same `id`.

## Foreign Key

A foreign key connects one table to another.

Example:

```text
transactions.user_id = users.id
```

This means the transaction belongs to that user.

If `transactions.user_id` points to a user that does not exist, the database
should reject it.

## Why Not Store Transactions Inside the User Row?

A user can have many transactions. If we tried to store all transactions inside
one user row, the data would become messy and hard to query.

Instead, we keep transactions in their own table. This lets us ask useful
questions later:

```text
Show all transactions for user 1.
Show all flagged transactions for user 1.
Show total spend this month.
Show average grocery spend.
```

## Why Money Uses DECIMAL

Money should use `DECIMAL`, not `FLOAT`.

`FLOAT` is good for scientific measurements where tiny precision differences are
acceptable. Money needs exact decimal behavior.

Example:

```text
Good: DECIMAL(12,2)
Avoid for money: FLOAT
```

## Why We Store AI Results in the Transaction Table

When a transaction is created, Spring Boot asks FastAPI for a risk score.

Then Spring Boot saves:

```text
risk_score
is_flagged
```

This is useful because the dashboard can read the saved result directly from the
database. The frontend does not need to call the AI service.

## The Important Mental Model

Think of the database as the memory of the application.

Think of Spring Boot as the rule enforcer.

Think of FastAPI as a calculator that gives Spring Boot a risk score.
