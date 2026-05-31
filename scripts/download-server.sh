#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SERVER_DIR="$PROJECT_DIR/mindustry-server-v7"
VERSION="v158.1"
SERVER_JAR="$SERVER_DIR/server-release.jar"
URL="https://github.com/Anuken/Mindustry/releases/download/$VERSION/server-release.jar"

if [ -f "$SERVER_JAR" ]; then
    echo "Mindustry server $VERSION already exists at $SERVER_JAR"
    read -rp "Download again? (y/N): " choice
    if [[ "$choice" != "y" && "$choice" != "Y" ]]; then
        echo "Skipping download."
        exit 0
    fi
fi

echo "=== Downloading Mindustry Server $VERSION ==="
echo "URL: $URL"

if command -v wget &> /dev/null; then
    wget -O "$SERVER_JAR" "$URL"
elif command -v curl &> /dev/null; then
    curl -L -o "$SERVER_JAR" "$URL"
else
    echo "Error: neither wget nor curl is available."
    exit 1
fi

echo ""
echo "Downloaded server jar to $SERVER_JAR"
