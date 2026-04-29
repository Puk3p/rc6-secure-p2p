#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

bash scripts/orchestrate.sh

echo ""
echo "Ready to run the local 3-node demo."
echo "Open three separate terminals and start the nodes in this order:"
echo ""
echo "  Terminal 1: java -jar node-app/target/node-app.jar node-configs/node-a.properties"
echo "  Terminal 2: java -jar node-app/target/node-app.jar node-configs/node-b.properties"
echo "  Terminal 3: java -jar node-app/target/node-app.jar node-configs/node-c.properties"
echo ""
echo "Then try from node-a:"
echo "  peers"
echo "  send node-b Salut de la node-a"
