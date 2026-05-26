#!/usr/bin/env bash
set -euo pipefail

echo "=== Polyglot AI — First-time setup ==="
echo ""

# ── Lefthook ──────────────────────────────────────────────
if ! command -v lefthook &>/dev/null; then
  echo "→ Installing Lefthook (git hooks manager)..."
  npm install -g lefthook
fi

if ! lefthook install --force &>/dev/null; then
  echo "→ Lefthook: installing hooks..."
  lefthook install
fi
echo "✓ Lefthook hooks installed (run 'lefthook run pre-commit' to test)"
echo ""

# ── Node / pnpm ───────────────────────────────────────────
if ! command -v pnpm &>/dev/null; then
  echo "→ Installing pnpm..."
  npm install -g pnpm
fi

echo "→ Installing app dependencies..."
cd "$(dirname "$0")/../app"
pnpm install
echo "✓ App dependencies installed"
echo ""

# ── Java ──────────────────────────────────────────────────
echo "→ Checking Java..."
if ! command -v java &>/dev/null; then
  echo "⚠  Java 21 not found. Install it and re-run this script."
fi
echo "✓ Java $(java -version 2>&1 | head -1)"
echo ""

# ── Docker ────────────────────────────────────────────────
echo "→ Checking Docker..."
if ! command -v docker &>/dev/null; then
  echo "⚠  Docker not found. Install Docker Desktop or engine and re-run this script."
else
  echo "✓ Docker found"
fi
echo ""

# ── Done ──────────────────────────────────────────────────
echo "=== Setup complete ==="
echo ""
echo "Next steps:"
echo "  cd server && docker compose up -d    # start databases"
echo "  cd server && ./gradlew bootRun       # start backend"
echo "  cd app && pnpm start                 # start mobile app"
