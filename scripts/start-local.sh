#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SERVER_DIR="$PROJECT_DIR/mindustry-server-v7"
SERVER_JAR="$SERVER_DIR/server-release.jar"

if [ ! -f "$SERVER_JAR" ]; then
    echo "Mindustry server jar not found at $SERVER_JAR"
    echo "Run ./scripts/download-server.sh first."
    exit 1
fi

echo "=== Building SectorizedPlugin ==="
cd "$PROJECT_DIR"
./gradlew jar

echo ""
echo "=== Starting Mindustry Server ==="
echo "The 'sectorized' command will be sent automatically after server startup."
echo ""

cd "$SERVER_DIR"
(sleep 5; echo "sectorized"; cat) | java -jar "$SERVER_JAR"
