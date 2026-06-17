#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DEPLOY_DIR="$PROJECT_DIR/deploy"
CONFIG_DIR="$DEPLOY_DIR/config/mods/config"

if [ ! -f "$DEPLOY_DIR/docker-compose.yaml" ]; then
    echo "=== Creating deploy infrastructure ==="
    mkdir -p "$DEPLOY_DIR"

    cat > "$DEPLOY_DIR/.env" <<EOF
MARIADB_ROOT_PASSWORD=root_password
MARIADB_DATABASE=sectorized
MARIADB_USER=admin
MARIADB_PASSWORD=password
EOF

    cat > "$DEPLOY_DIR/docker-compose.yaml" <<'EOF'
services:
  mariadb:
    build:
      context: .
      dockerfile: Dockerfile.mariadb
    container_name: sectorized-mariadb
    restart: unless-stopped
    environment:
      MARIADB_ROOT_PASSWORD: ${MARIADB_ROOT_PASSWORD}
      MARIADB_DATABASE: ${MARIADB_DATABASE}
      MARIADB_USER: ${MARIADB_USER}
      MARIADB_PASSWORD: ${MARIADB_PASSWORD}
    volumes:
      - ./db_data:/var/lib/mysql
    healthcheck:
      test: [ "CMD", "mariadb-admin", "ping", "-h", "localhost", "-u", "root", "-p${MARIADB_ROOT_PASSWORD}" ]
      interval: 3s
      timeout: 3s
      retries: 10
    networks:
      - sectorized-network

  mindustry:
    build: .
    container_name: sectorized-mindustry
    restart: unless-stopped
    depends_on:
      mariadb:
        condition: service_healthy
    ports:
      - "6567:6567/tcp"
      - "6567:6567/udp"
    volumes:
      - ./config:/server/config
    stdin_open: true
    tty: true
    networks:
      - sectorized-network

networks:
  sectorized-network:
    driver: bridge
EOF

    cat > "$DEPLOY_DIR/Dockerfile" <<'EOF'
FROM eclipse-temurin:17-alpine
RUN apk add --no-cache curl
WORKDIR /server
RUN curl -L -o server.jar https://github.com/Anuken/Mindustry/releases/download/v158.1/server-release.jar
COPY start.sh /server/start.sh
RUN chmod +x /server/start.sh
EXPOSE 6567/tcp 6567/udp
CMD ["/server/start.sh"]
EOF

    cat > "$DEPLOY_DIR/Dockerfile.mariadb" <<'EOF'
FROM mariadb:10.11
COPY init.sql /docker-entrypoint-initdb.d/
EOF

    cat > "$DEPLOY_DIR/init.sql" <<'EOF'
CREATE DATABASE IF NOT EXISTS sectorized;
USE sectorized;
CREATE TABLE IF NOT EXISTS ranking (
    uuid VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    score INT DEFAULT 0,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    discordTag VARCHAR(255),
    empty INT DEFAULT 0
);
EOF

    echo "Created deploy/ with default infrastructure files."
fi

echo "=== Creating config directory ==="
mkdir -p "$CONFIG_DIR"

echo "=== Writing database config ==="
cat > "$CONFIG_DIR/sectorized-database-config.json" <<EOF
{
  "url": "jdbc:mariadb://mariadb:3306/sectorized",
  "user": "admin",
  "password": "password"
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
  run --rm --service-ports --no-deps -it \
  mindustry

echo "=== Stopping MariaDB ==="
docker compose --env-file "$DEPLOY_DIR/.env" -f "$DEPLOY_DIR/docker-compose.yaml" down
