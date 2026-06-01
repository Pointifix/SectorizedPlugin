#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DEPLOY_DIR="$PROJECT_DIR/deploy"
CONFIG_DIR="$DEPLOY_DIR/config/mods/config"

echo "=== Creating config directory ==="
rm -rf "$CONFIG_DIR"
mkdir -p "$CONFIG_DIR"

echo "=== Writing database config ==="
cat > "$CONFIG_DIR/sectorized-database-config.json" <<EOF
{
  "url": "jdbc:mariadb://mariadb:3306/sectorized",
  "user": "sectorized",
  "password": "local_dev_user_pw"
}
EOF

echo "=== Starting MariaDB ==="
docker compose --env-file "$DEPLOY_DIR/.env" -f "$DEPLOY_DIR/docker-compose.yaml" up -d mariadb

echo "=== Building SectorizedPlugin ==="
cd "$PROJECT_DIR"
./gradlew jar

echo "=== Starting Mindustry Server ==="
echo "The 'sectorized' command will be sent automatically after server startup."
echo ""

cd "$PROJECT_DIR"
docker compose --env-file "$DEPLOY_DIR/.env" -f "$DEPLOY_DIR/docker-compose.yaml" \
  run --rm --service-ports --no-deps \
  mindustry

echo "=== Stopping MariaDB ==="
docker compose --env-file "$DEPLOY_DIR/.env" -f "$DEPLOY_DIR/docker-compose.yaml" down
