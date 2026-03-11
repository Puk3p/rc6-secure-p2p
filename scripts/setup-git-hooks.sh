#!/usr/bin/env bash
set -e

chmod +x .githooks/pre-commit
chmod +x .githooks/commit-msg
chmod +x .githooks/pre-push

git config core.hooksPath .githooks

echo "Git hooks installed successfully."
echo "Configured hooks path: $(git config core.hooksPath)"