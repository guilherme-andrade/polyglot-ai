#!/usr/bin/env bash
set -euo pipefail

# ─── Polyglot AI — Development Environment Setup ─────────────────────────────
# Installs all dependencies, configures MCPs, and verifies the environment.
# Safe to re-run — already-installed tools are skipped.

# ─── Colors & Helpers ────────────────────────────────────────────────────────

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

TOTAL_STEPS=12
CURRENT_STEP=0

step() {
  CURRENT_STEP=$((CURRENT_STEP + 1))
  echo -e "\n${BOLD}${BLUE}[$CURRENT_STEP/$TOTAL_STEPS]${NC} ${BOLD}$1${NC}"
}

ok()   { echo -e "  ${GREEN}✔${NC} $1"; }
warn() { echo -e "  ${YELLOW}⚠${NC} $1"; }
fail() { echo -e "  ${RED}✘${NC} $1"; }
info() { echo -e "  ${CYAN}ℹ${NC} $1"; }

check_cmd() { command -v "$1" &>/dev/null; }

print_banner() {
  echo -e "${BOLD}${BLUE}"
  echo "  ╔══════════════════════════════════════╗"
  echo "  ║   🦜  Polyglot AI — Dev Setup        ║"
  echo "  ╚══════════════════════════════════════╝${NC}"
  echo
  echo -e "  This script installs everything needed to work on Polyglot AI."
  echo -e "  It is safe to re-run — existing tools are skipped."
  echo
}

# ─── Version Requirements ────────────────────────────────────────────────────

REQUIRED_JAVA="21"
REQUIRED_NODE="22"
REQUIRED_PNPM="9"

# ─── Main ────────────────────────────────────────────────────────────────────

print_banner

# ── Step 1: Platform check ───────────────────────────────────────────────────

step "Checking platform"

OS="$(uname -s)"
ARCH="$(uname -m)"

if [[ "$OS" != "Darwin" ]]; then
  warn "This script is designed for macOS. Some steps may not work on $OS."
  echo -e "  Continue anyway? (y/N)"
  read -r cont
  [[ "$cont" =~ ^[Yy]$ ]] || exit 0
fi

ok "Running on $OS ($ARCH)"

# ── Step 2: Xcode CLI Tools ──────────────────────────────────────────────────

step "Checking Xcode Command Line Tools"

if xcode-select -p &>/dev/null; then
  ok "Xcode CLI tools already installed"
else
  info "Installing Xcode Command Line Tools..."
  xcode-select --install 2>/dev/null || true
  echo -e "  ${YELLOW}Press enter once the installation completes...${NC}"
  read -r
fi

# ── Step 3: Homebrew ─────────────────────────────────────────────────────────

step "Checking Homebrew"

if check_cmd brew; then
  ok "Homebrew found ($(brew --version | head -1))"
  info "Updating Homebrew..."
  brew update --quiet 2>/dev/null || true
else
  info "Installing Homebrew..."
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
  # Add to path for this session
  eval "$(/opt/homebrew/bin/brew shellenv 2>/dev/null || /usr/local/bin/brew shellenv 2>/dev/null)" || true
  ok "Homebrew installed"
fi

# ── Step 4: Java 21 ──────────────────────────────────────────────────────────

step "Checking Java 21 (Temurin)"

JAVA_OK=false
if check_cmd java; then
  JAVA_VER=$(java -version 2>&1 | head -1 | grep -oE '[0-9]+' | head -1 || echo "0")
  if [[ "$JAVA_VER" -ge "$REQUIRED_JAVA" ]]; then
    ok "Java $JAVA_VER found ($(which java))"
    JAVA_OK=true
  else
    warn "Java $JAVA_VER found but Java $REQUIRED_JAVA+ is required"
  fi
fi

if [[ "$JAVA_OK" == "false" ]]; then
  info "Installing Java 21 via Homebrew..."
  brew install --cask temurin@21 2>/dev/null || brew install openjdk@21
  info "Java 21 installed. You may need to restart your shell."
  export JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || echo '')"
  ok "Java 21 installed"
fi

# ── Step 5: Node.js ──────────────────────────────────────────────────────────

step "Checking Node.js 22"

NODE_OK=false
if check_cmd node; then
  NODE_VER=$(node -v | grep -oE '[0-9]+' | head -1)
  if [[ "$NODE_VER" -ge "$REQUIRED_NODE" ]]; then
    ok "Node.js $NODE_VER found ($(which node))"
    NODE_OK=true
  else
    warn "Node.js v$NODE_VER found but v$REQUIRED_NODE+ is required"
  fi
fi

if [[ "$NODE_OK" == "false" ]]; then
  if [[ -d "$HOME/.nvm" ]] || check_cmd nvm; then
    info "nvm detected — installing Node.js 22 via nvm..."
    export NVM_DIR="$HOME/.nvm"
    [ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"
    nvm install 22
    nvm use 22
  else
    info "Installing Node.js 22 via Homebrew..."
    brew install node@22
  fi
  ok "Node.js installed"
fi

# ── Step 6: pnpm ─────────────────────────────────────────────────────────────

step "Checking pnpm"

if check_cmd pnpm; then
  PNPM_VER=$(pnpm -v)
  ok "pnpm $PNPM_VER found"
else
  info "Installing pnpm via npm..."
  npm install -g pnpm
  ok "pnpm installed ($(pnpm -v))"
fi

# ── Step 7: Docker ────────────────────────────────────────────────────────────

step "Checking Docker"

if check_cmd docker && docker info &>/dev/null; then
  ok "Docker found and running ($(docker --version))"
else
  warn "Docker not found or not running"
  info "Install Docker Desktop from: https://www.docker.com/products/docker-desktop/"
  info "After installing, start Docker Desktop and re-run this script."
fi

# ── Step 8: GitHub CLI ───────────────────────────────────────────────────────

step "Checking GitHub CLI"

if check_cmd gh; then
  ok "gh CLI found ($(gh --version | head -1))"
  if gh auth status &>/dev/null; then
    ok "gh authenticated as $(gh auth status 2>&1 | grep -oE 'Logged in to [^ ]+ as ([^ ]+)' | grep -oE 'as .*' | cut -d' ' -f2 || echo 'user')"
  else
    warn "gh not authenticated — run 'gh auth login'"
  fi
else
  info "Installing GitHub CLI..."
  brew install gh
  ok "gh CLI installed"
  info "Run 'gh auth login' to authenticate"
fi

# ── Step 9: Terraform & Ansible ──────────────────────────────────────────────

step "Checking Terraform & Ansible"

if check_cmd terraform; then
  ok "Terraform found ($(terraform version -json 2>/dev/null | grep -oE '"terraform_version":"[^"]+"' | cut -d'"' -f4 || terraform --version | head -1))"
else
  info "Installing Terraform..."
  brew install terraform
  ok "Terraform installed ($(terraform --version | head -1))"
fi

if check_cmd ansible; then
  ok "Ansible found ($(ansible --version | head -1))"
else
  info "Installing Ansible..."
  brew install ansible
  ok "Ansible installed ($(ansible --version | head -1))"
fi

# ── Step 10: Lefthook (pre-commit) ────────────────────────────────────────────

step "Checking Lefthook"

if check_cmd lefthook; then
  ok "Lefthook found ($(lefthook version))"
else
  info "Installing Lefthook..."
  brew install lefthook
  ok "Lefthook installed ($(lefthook version))"
fi

# ── Step 11: EAS CLI (Expo) ──────────────────────────────────────────────────

step "Checking EAS CLI"

if check_cmd eas; then
  ok "EAS CLI found ($(eas --version 2>/dev/null || echo 'installed'))"
else
  info "Installing EAS CLI..."
  npm install -g eas-cli
  ok "EAS CLI installed ($(eas --version 2>/dev/null || echo 'installed'))"
fi

# ── Step 12: MCPs ────────────────────────────────────────────────────────────

step "Configuring Claude Code MCPs"

MCP_DIR="$(git rev-parse --show-toplevel 2>/dev/null || echo "$(pwd)")/.claude"
MCP_FILE="$MCP_DIR/mcp.json"

info "Writing MCP configuration to .claude/mcp.json..."

cat > "$MCP_FILE" << 'MCFG'
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": ["-y", "@playwright/mcp", "--headless"],
      "description": "Browser automation for testing the Expo web app"
    },
    "openspec": {
      "command": "npx",
      "args": ["-y", "@anthropic-ai/openspec"],
      "description": "OpenSpec — write, validate, and manage feature specifications"
    }
  }
}
MCFG

ok "MCP configuration written"
info "MCPs configured:"
info "  playwright  → @playwright/mcp (browser testing)"
info "  openspec    → @anthropic-ai/openspec (spec management)"

# ── Summary ──────────────────────────────────────────────────────────────────

echo
echo -e "${BOLD}${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BOLD}${GREEN}  Setup complete!${NC}"
echo
echo -e "  ${BOLD}Next steps:${NC}"
echo -e "  1. Authenticate:  ${CYAN}gh auth login${NC}  (if not already done)"
echo -e "  2. Start Docker:   ${CYAN}open -a Docker${NC}   (if not running)"
echo -e "  3. Install deps:   ${CYAN}cd app && pnpm install${NC}"
echo -e "  4. Start server:   ${CYAN}cd server && docker compose up -d && ./gradlew bootRun${NC}"
echo -e "  5. Start app:      ${CYAN}cd app && pnpm start${NC}"
echo
echo -e "  ${BOLD}Test everything:${NC}"
echo -e "  ${CYAN}./scripts/verify.sh${NC}"
echo

# ── Verify key tools ──────────────────────────────────────────────────────────

echo -e "${BOLD}Installed versions:${NC}"
echo

report() {
  local name="$1"; shift
  if check_cmd "$1"; then
    printf "  ${GREEN}%-16s${NC} %s\n" "$name" "$("$@")"
  else
    printf "  ${RED}%-16s${NC} not found\n" "$name"
  fi
}

report "java"        java --version 2>&1 | head -1
report "node"        node --version
report "pnpm"        pnpm --version
report "docker"      docker --version 2>/dev/null || echo "not running"
report "gh"          gh --version 2>&1 | head -1
report "terraform"   terraform --version | head -1
report "ansible"     ansible --version | head -1
report "lefthook"    lefthook version
report "eas"         eas --version 2>/dev/null || echo "not found"

echo
