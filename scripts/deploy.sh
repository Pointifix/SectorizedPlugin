#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DEPLOY_DIR="$PROJECT_DIR/deploy"

if [ $# -lt 1 ]; then
    echo "Usage: $0 <nas-mount-path>"
    echo ""
    echo "Copies deploy/ files to the NAS Sectorized folder (excluding .env)."
    echo ""
    echo "Example:"
    echo "  $0 /run/user/1000/gvfs/smb-share:server=nas-simon.local,share=docker/Sectorized"
    exit 1
fi

NAS_PATH="$1"

if [ ! -d "$NAS_PATH" ]; then
    echo "Error: NAS path not found: $NAS_PATH"
    echo "Make sure the SMB share is mounted."
    exit 1
fi

echo "=== Deploying to $NAS_PATH ==="

# Back up sectorized-discord-config.json to avoid overwriting real credentials
if [ -f "$NAS_PATH/sectorized-discord-config.json" ]; then
    cp "$NAS_PATH/sectorized-discord-config.json" /tmp/sectorized-discord-config.json.nas.backup
fi

rsync -av --exclude='.env' "$DEPLOY_DIR/" "$NAS_PATH/"

# Restore if the NAS version had custom content
if [ -f /tmp/sectorized-discord-config.json.nas.backup ]; then
    if ! cmp -s /tmp/sectorized-discord-config.json.nas.backup "$DEPLOY_DIR/sectorized-discord-config.json"; then
        cp /tmp/sectorized-discord-config.json.nas.backup "$NAS_PATH/sectorized-discord-config.json"
        echo "Restored existing sectorized-discord-config.json on NAS (preserving real credentials)."
    fi
    rm /tmp/sectorized-discord-config.json.nas.backup
fi

echo ""
echo "=== Deploy complete ==="
echo ""
echo "Files deployed:"
ls -la "$NAS_PATH/" | grep -v ".env$"
