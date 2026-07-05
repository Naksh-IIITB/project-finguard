# Learning Notes - Step 4: Transaction API Without AI

## What We Built

Step 4 adds the first real backend feature: saving and reading transactions.

The API can now support:

```http
POST /api/transactions
GET /api/transactions?userId=1
GET /api/transactions/1
```

This step does not call the AI service yet. New transactions are saved with:

```text
flagged = false
riskScore = 0.0
```

That gives us a working transaction pipeline first. AI will be inserted into
that pipeline later.

## Request Flow

When the frontend sends a transaction, the backend flow is:

```text
TransactionController
  -> TransactionService
  -> UserRepository
  -> TransactionRepository
  -> PostgreSQL
```

## Controller

The controller is the HTTP layer.

It answers questions like:

```text
Which URL was called?
Was it a GET or POST?
What JSON came in?
What JSON should go out?
```

Our transaction controller listens under:

```text
/api/transactions
```

## DTO

DTO means Data Transfer Object.

DTOs are the shapes of the data that enter or leave the API.

We added:

```text
CreateTransactionRequest
TransactionResponse
```

The request DTO uses validation annotations like:

```java
@NotNull
@NotBlank
@DecimalMin("0.01")
```

This means invalid requests can be rejected before they reach the service layer.

## Service

The service contains business logic.

For creating a transaction, the service:

1. Looks up the user.
2. Throws an error if the user does not exist.
3. Creates a transaction object.
4. Saves it through the repository.
5. Converts it into a response DTO.

This keeps the controller small and focused.

## Repository

Repositories are Spring Data interfaces.

They give us common database operations without writing SQL manually:

```java
findById
save
existsById
```

We also added:

```java
findByUserIdOrderByTransactionTimeDesc
```

Spring Data reads that method name and creates the query automatically.

## Entity

Entities are Java classes that map to database tables.

We added:

```text
User -> users table
Transaction -> transactions table
```

The `Transaction` entity has a `ManyToOne` relationship with `User`.

That means:

```text
many transactions can belong to one user
```

## Migration

Flyway migrations are versioned SQL files.

We added:

```text
V1__create_users_table.sql
V2__create_transactions_table.sql
```

Flyway runs them in order. This matters because `transactions` depends on
`users`, so the user table must exist first.

## Why We Did Not Add AI Yet

A good backend is built in layers.

First we prove this works:

```text
request -> validation -> service -> database -> response
```

After that, we can insert AI scoring into the service:

```text
request -> validation -> service -> AI -> database -> response
```

That is easier to understand and easier to debug.
