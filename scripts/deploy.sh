#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

export MINDUSTRY_DEPLOY_PATH="/run/user/1000/gvfs/smb-share:server=nas-simon.local,share=docker/Sectorized"

echo "=== Building and Deploying SectorizedPlugin (v158.1) ==="
cd "$PROJECT_DIR"
./gradlew deploy

echo ""
echo "=== Deployment Complete ==="
echo "To run the server on your NAS:"
echo "  1. SSH into your NAS or navigate to the folder:"
echo "     /volume1/docker/Sectorized (or actual path on NAS)"
echo "  2. Start the containers using:"
echo "     docker compose up -d --build"
echo "  3. To restart the mindustry container to reload mods:"
echo "     docker compose restart mindustry"
echo "  4. To attach to the Mindustry console:"
echo "     docker attach sectorized-mindustry"
