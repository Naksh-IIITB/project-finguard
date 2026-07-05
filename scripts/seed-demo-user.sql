INSERT INTO users (username, email, password_hash, created_at, updated_at)
VALUES (
  'demo',
  'demo@finguard.local',
  'not-a-real-password-hash-yet',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;
