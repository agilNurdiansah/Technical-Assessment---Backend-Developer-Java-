INSERT INTO users (name, email, status, created_at, updated_at)
VALUES
  ('Alice Johnson', 'alice@example.com', 'ACTIVE', NOW(), NOW()),
  ('Bob Smith', 'bob@example.com', 'INACTIVE', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
