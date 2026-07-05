#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "Checking Spring Boot backend"
cd "$ROOT_DIR/finguard-core-backend"
mvn test

echo "Checking FastAPI AI engine"
cd "$ROOT_DIR/finguard-ai-engine"
if [ ! -d ".venv" ]; then
  python3 -m venv .venv
fi
.venv/bin/pip install -r requirements.txt
.venv/bin/pytest

echo "Checking React frontend"
cd "$ROOT_DIR/finguard-frontend"
npm install
npm run build

echo "All local checks passed"
