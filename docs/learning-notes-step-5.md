# Learning Notes - Step 5: User/Auth Layer

## What We Built

Step 5 adds authentication.

Before this step, transaction creation trusted this field:

```json
{
  "userId": 1
}
```

That is not safe. A user could change the number and try to create or read data
as someone else.

After this step, the frontend sends a JWT token instead:

```http
Authorization: Bearer <token>
```

The backend verifies the token and figures out the real user.

## New Endpoints

```http
POST /api/auth/register
POST /api/auth/login
```

Registration creates a user and returns a token.

Login verifies credentials and returns a token.

## Password Hashing

We never store raw passwords.

Instead of saving:

```text
password123
```

The backend saves a BCrypt hash.

When a user logs in, Spring Security compares the submitted password with the
stored hash.

## JWT

JWT means JSON Web Token.

In this project, the token contains signed information like:

```text
username
userId
email
issued time
expiration time
```

The token is signed using a secret. If someone changes the token, the signature
check fails.

## Security Filter

The JWT filter runs before controllers.

Its job is:

1. Read the `Authorization` header.
2. Check whether it starts with `Bearer `.
3. Extract the token.
4. Validate the token.
5. Put the authenticated user into Spring Security's context.

After that, controllers can receive the current user with:

```java
@AuthenticationPrincipal UserPrincipal userPrincipal
```

## Protected Transactions

Transaction APIs are now protected.

Create transaction changed from:

```json
{
  "userId": 1,
  "amount": 2500.0
}
```

to:

```json
{
  "amount": 2500.0
}
```

The backend gets the user ID from the token.

## Important Security Upgrade

We also scoped single transaction lookup by user:

```text
findByIdAndUserId(transactionId, authenticatedUserId)
```

That prevents one user from reading another user's transaction by guessing an ID.

## Mental Model

The request flow is now:

```text
Request
  -> JWT filter
  -> Security context
  -> Controller
  -> Service
  -> Repository
  -> Database
```

Authentication happens before controller code runs.
