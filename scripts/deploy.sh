#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DEPLOY_DIR="$PROJECT_DIR/deploy"

if [ $# -lt 1 ]; then
    echo "Usage: $0 <nas-mount-path>"
    echo ""
    echo "Copies deploy/ infrastructure files and plugin mod to the NAS Sectorized folder."
    echo "Excludes: .env, config/ (except config/mods/), db_data/."
    echo ""
    echo "Example:"
    echo "  $0 /run/user/1000/gvfs/smb-share:server=nas-simon.local,share=docker/Sectorized"
    exit 1
fi

NAS_PATH="$1"

if [ ! -d "$NAS_PATH" ]; then
    if [[ $EUID -eq 0 && "$NAS_PATH" == /run/user/* ]]; then
        echo "Error: NAS path not found: $NAS_PATH"
        echo ""
        echo "You are running as root (sudo), but the NAS is mounted under your user's GVFS"
        echo "directory which is not accessible as root."
        echo ""
        echo "Run without sudo instead:"
        echo "  $0 $NAS_PATH"
    else
        echo "Error: NAS path not found: $NAS_PATH"
        echo "Make sure the SMB share is mounted."
    fi
    exit 1
fi

echo "=== Deploying to $NAS_PATH ==="

rsync -av --no-perms --no-owner --no-group \
  --exclude='.env' --exclude='/config/' --exclude='/db_data/' \
  "$DEPLOY_DIR/" "$NAS_PATH/" || true

rsync -av --no-perms --no-owner --no-group \
  --exclude='/config/' \
  "$DEPLOY_DIR/config/mods/" "$NAS_PATH/config/mods/" || true

echo ""
echo "=== Deploy complete ==="
echo ""
echo "Files deployed:"
ls -la "$NAS_PATH/" | grep -v ".env$"
