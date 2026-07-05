CREATE TABLE transactions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  category VARCHAR(80) NOT NULL,
  merchant VARCHAR(120) NOT NULL,
  transaction_time TIMESTAMP NOT NULL,
  is_flagged BOOLEAN NOT NULL DEFAULT FALSE,
  risk_score DOUBLE PRECISION NOT NULL DEFAULT 0 CHECK (risk_score >= 0 AND risk_score <= 1),
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_user_time ON transactions(user_id, transaction_time DESC);
CREATE INDEX idx_transactions_user_flagged ON transactions(user_id, is_flagged);
