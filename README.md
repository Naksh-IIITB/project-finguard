# FinGuard - Smart Expense Tracker

FinGuard is a learning-focused fintech project that simulates a smart expense tracker.
The backend owns the business rules and database, while a separate AI service scores
transactions for unusual or suspicious behavior.

## Services

- `finguard-core-backend`: Spring Boot API, authentication, transaction logic, and database access.
- `finguard-ai-engine`: FastAPI service that scores transactions using an anomaly detection model.
- `finguard-frontend`: React/Vue client for dashboards, forms, and transaction review.
- `docs`: Architecture notes, API contracts, and database design.

## Target Flow

```text
Frontend -> Spring Boot Backend -> PostgreSQL
                         |
                         v
                  FastAPI AI Service -> model.pkl
```

## Learning Path

1. Build the project skeleton.
2. Design the database schema.
3. Create the Spring Boot backend.
4. Add transaction APIs.
5. Add users and authentication.
6. Add spending profile calculations.
7. Create the FastAPI AI service.
8. Connect Spring Boot to FastAPI.
9. Add a real anomaly detection model.
10. Build the frontend.
11. Run everything with Docker Compose.

## Backend Build

From the Spring Boot backend folder:

```bash
cd finguard-core-backend
mvn test
```

This compiles the backend and runs tests. The full server now expects PostgreSQL
because the backend has database migrations and real entities.

## Step 4 Transaction API

The backend now has transaction endpoints:

```http
POST /api/transactions
GET /api/transactions
GET /api/transactions/1
```

These endpoints are protected after Step 5. Call `POST /api/auth/register` or
`POST /api/auth/login` first, then send `Authorization: Bearer <token>`.

## Step 5 Auth API

The backend now has auth endpoints:

```http
POST /api/auth/register
POST /api/auth/login
```

Passwords are hashed with BCrypt before being stored. The raw password should
never be saved in the database.

## Step 6 Spending Profile API

The backend now calculates spending profile metrics:

```http
GET /api/profile/spending
GET /api/profile/transaction-context?amount=2500.00&category=Entertainment
```

Both endpoints require `Authorization: Bearer <token>`.

## Step 7 AI Service

The Python AI service exposes:

```http
GET /health
POST /predict
```

Run it locally with:

```bash
cd finguard-ai-engine
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

## Step 8 Backend to AI Integration

Transaction creation now calls the AI service before saving:

```text
POST /api/transactions
  -> Spring Boot builds AI features
  -> Spring Boot calls FastAPI POST /predict
  -> Spring Boot saves riskScore and flagged
```

Run the AI service on port `8000` before testing transaction creation locally.

## Step 9 ML Model

The AI service now loads a saved Isolation Forest model:

```text
finguard-ai-engine/ml/model.pkl
```

Retrain the model with:

```bash
cd finguard-ai-engine
.venv/bin/python ml/train_model.py
```

## Step 10 Frontend

The React frontend lives in:

```text
finguard-frontend
```

Run it with:

```bash
cd finguard-frontend
npm install
npm run dev
```

The frontend talks to Spring Boot at `http://localhost:8080` by default. Override
that with `VITE_API_BASE_URL` if needed.
