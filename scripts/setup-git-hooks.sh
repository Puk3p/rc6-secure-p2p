#!/usr/bin/env bash
set -e

chmod +x .githooks/pre-commit
chmod +x .githooks/commit-msg
chmod +x .githooks/pre-push
chmod +x scripts/fix-eof.sh

git config core.hooksPath .githooks

echo "Git hooks installed successfully."
echo "Configured hooks path: $(git config core.hooksPath)"
echo ""
echo "Available scripts:"
echo "  bash scripts/fix-eof.sh   - fix missing final newlines (macOS/Linux)"
echo "  powershell scripts/fix-eof.ps1 - fix missing final newlines (Windows)"
