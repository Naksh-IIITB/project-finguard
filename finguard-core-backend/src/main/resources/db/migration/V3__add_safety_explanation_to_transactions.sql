ALTER TABLE transactions
  ADD COLUMN risk_level VARCHAR(24) NOT NULL DEFAULT 'Stable',
  ADD COLUMN what_changed VARCHAR(320) NOT NULL DEFAULT 'Stable signal: pattern is close to your normal transaction history.',
  ADD COLUMN safest_next_step VARCHAR(360) NOT NULL DEFAULT 'No action needed; keep monitoring for changes.';
