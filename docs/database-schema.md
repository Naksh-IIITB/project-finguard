# Database Schema

The backend will own the database schema. We will implement this later with migrations.

## users

| Column | Type | Notes |
| --- | --- | --- |
| id | UUID or BIGSERIAL | Primary key |
| username | VARCHAR | Unique username |
| password_hash | VARCHAR | Hashed password, never plain text |
| email | VARCHAR | Unique email |
| created_at | TIMESTAMP | Account creation time |

## transactions

| Column | Type | Notes |
| --- | --- | --- |
| id | UUID or BIGSERIAL | Primary key |
| user_id | FK | Owner of the transaction |
| amount | DECIMAL | Transaction amount |
| category | VARCHAR | Spending category |
| merchant | VARCHAR | Merchant or payee |
| timestamp | TIMESTAMP | Transaction time |
| is_flagged | BOOLEAN | Whether AI flagged it |
| risk_score | FLOAT | AI risk score |

