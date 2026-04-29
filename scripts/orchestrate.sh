#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

run_step() {
  local title="$1"
  shift
  echo ""
  echo "==> $title"
  "$@"
}

run_maven() {
  if [ -f "./mvnw" ]; then
    chmod +x ./mvnw 2>/dev/null || true
    ./mvnw "$@"
  elif command -v mvn >/dev/null 2>&1; then
    mvn "$@"
  else
    echo "Error: Maven not found. Install Maven or use the Maven Wrapper."
    exit 1
  fi
}

echo "RC6 Secure P2P orchestrator (macOS/Linux)"
echo "Project root: $ROOT_DIR"

run_step "Install Git hooks" bash scripts/setup-git-hooks.sh
run_step "Normalize final newlines" bash scripts/fix-eof.sh
run_step "Apply Java formatting" run_maven spotless:apply --batch-mode --no-transfer-progress
run_step "Check formatting" run_maven spotless:check --batch-mode --no-transfer-progress
run_step "Compile all modules" run_maven compile --batch-mode --no-transfer-progress
run_step "Run tests" run_maven test --batch-mode --no-transfer-progress
run_step "Package application" run_maven package -DskipTests --batch-mode --no-transfer-progress

echo ""
echo "Orchestration completed successfully."
