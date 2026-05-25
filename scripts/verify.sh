#!/usr/bin/env bash
set -euo pipefail

# ─── Polyglot AI — Environment Verification ──────────────────────────────────
# Verifies the development environment is correctly set up.
# Run this after setup.sh, or any time to check your environment health.

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
PASS=0; FAIL=0

check() {
  local name="$1"; shift
  if "$@" &>/dev/null; then
    echo -e "  ${GREEN}✔${NC} $name"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}✘${NC} $name"
    FAIL=$((FAIL + 1))
  fi
}

echo -e "${CYAN}Polyglot AI — Environment Verification${NC}"
echo

echo "System:"
check "macOS"              [[ "$(uname -s)" == "Darwin" ]]
echo

echo "Core tools:"
check "java 21+"           bash -c 'java -version 2>&1 | grep -qE "version \"(21|22|23|[2-9][0-9])"'
check "node 22+"           bash -c '[[ $(node -v | grep -oE "[0-9]+" | head -1) -ge 22 ]]'
check "pnpm"               command -v pnpm
check "docker (running)"   docker info
check "gh (auth)"          gh auth status
echo

echo "Infrastructure:"
check "terraform"           command -v terraform
check "ansible"             command -v ansible
check "lefthook"            command -v lefthook
check "eas CLI"             command -v eas
echo

echo "MCPs (.claude/mcp.json):"
check "playwright MCP"     bash -c 'grep -q "@playwright/mcp" .claude/mcp.json 2>/dev/null || grep -q "@playwright/mcp" "$(git rev-parse --show-toplevel 2>/dev/null)/.claude/mcp.json" 2>/dev/null'
check "openspec MCP"       bash -c 'grep -q "@anthropic-ai/openspec" .claude/mcp.json 2>/dev/null || grep -q "@anthropic-ai/openspec" "$(git rev-parse --show-toplevel 2>/dev/null)/.claude/mcp.json" 2>/dev/null'
echo

echo "Project setup:"
check "repo cloned"        bash -c 'git rev-parse --show-toplevel &>/dev/null'
check "app/ dir exists"    bash -c '[ -d "$(git rev-parse --show-toplevel 2>/dev/null || echo ".")/app" ]'
check "server/ dir exists" bash -c '[ -d "$(git rev-parse --show-toplevel 2>/dev/null || echo ".")/server" ]'
echo

echo -e "${CYAN}Results: ${GREEN}$PASS passed${NC}, ${RED}$FAIL failed${NC}"

if [[ "$FAIL" -gt 0 ]]; then
  echo
  echo -e "${YELLOW}Run ./scripts/setup.sh to install missing dependencies.${NC}"
  exit 1
else
  echo
  echo -e "${GREEN}All checks passed — ready to develop!${NC}"
fi
