# Learning Notes - Step 3: Spring Boot Backend Setup

## What We Created

Step 3 creates the Java backend project inside:

```text
finguard-core-backend
```

This backend will eventually receive API requests from the frontend, validate
data, talk to PostgreSQL, call the FastAPI AI service, and return results.

## What `pom.xml` Does

`pom.xml` is the Maven project file.

Maven uses it to answer questions like:

```text
What is this project called?
Which Java version should it use?
Which libraries does it need?
How should it be built?
```

In this project, `pom.xml` adds Spring Boot libraries for:

- web APIs
- database access with JPA
- validation
- PostgreSQL
- Flyway migrations
- tests

## Main Application Class

The main class is:

```text
FinguardCoreApplication.java
```

This is the entry point. When Java runs this class, Spring Boot starts the
backend server.

The important annotation is:

```java
@SpringBootApplication
```

That tells Spring Boot to scan this package, configure the app, and start the
server.

## Why We Use Packages

The backend is grouped by responsibility:

```text
controller  receives HTTP requests
service     holds business logic
repository  talks to the database
entity      represents database tables
dto         represents request and response shapes
config      holds application configuration
security    will hold authentication/JWT code later
exception   will hold error handling code
```

The mental model is:

```text
Controller -> Service -> Repository -> Database
```

Requests should not jump straight from controller to database logic in larger
apps. The service layer keeps business rules in one place.

## Why We Added `/api/health`

The health endpoint is a tiny test endpoint:

```http
GET /api/health
```

It lets us check whether the backend server is running before we build the real
transaction APIs.

## What `application.yml` Does

`application.yml` stores backend configuration:

```text
server port
database URL
database username/password
Flyway setting
AI service URL
```

This keeps settings separate from Java code.

## What We Did Not Build Yet

We did not add users, transactions, authentication, or AI integration yet.

This step is only the backend foundation.
