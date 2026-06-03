# Deployment

## Prerequisites

The target server runs Docker with `docker compose`. The `deploy.sh` script copies files to the server over an **SMB mount** (e.g. GVFS). Mount the server's Docker share first, then pass the mount path to `deploy.sh`.

---

## Quick Reference

```bash
# === On development machine ===

# Build the plugin and deploy everything to the server
./gradlew jar
./scripts/deploy.sh /path/to/smb/mount

# === On the server ===

# View logs
docker logs --tail 1000 --follow sectorized-mindustry

# Stop all containers
docker compose down

# Start all containers
docker compose up -d

# Restart only mindustry (fast, no rebuild)
docker compose restart mindustry
```

---

## What to rebuild for each change

| Changed file          | Action needed on the server                          |
|-----------------------|------------------------------------------------------|
| `start.sh`            | `docker compose build mindustry && docker compose up -d` |
| `Dockerfile`          | `docker compose build mindustry && docker compose up -d` |
| `SectorizedPlugin.jar`| `docker compose restart mindustry` (no build needed, jar is a volume mount) |
| `Dockerfile.mariadb`  | `docker compose build --no-cache mariadb && docker compose up -d` |
| `init.sql`            | `docker compose build --no-cache mariadb && docker compose down && rm -rf db_data/ && docker compose up -d` |
| `.env`                | `docker compose down && rm -rf db_data/ && docker compose up -d` |
| `docker-compose.yaml` | `docker compose up -d` (Docker detects changed config) |

---

## Full deployment flow

### 1. Build and deploy

```bash
# Mount the SMB share (if not already mounted), then:
./gradlew jar
./scripts/deploy.sh /run/user/1000/gvfs/smb-share:server=nas-simon.local,share=docker/Sectorized
```

This copies `deploy/` to the server, excluding `.env`, `config/`, `db_data/`, and `config/mods/config/`. Only `SectorizedPlugin.jar` is copied from `config/mods/` (the config JSON files are kept as-is on the server).

### 2. On the server, rebuild if needed

```bash
cd /volume1/docker/Sectorized

# If start.sh or Dockerfile changed:
docker compose build mindustry

# If init.sql or Dockerfile.mariadb changed (and schema needs recreation):
docker compose build --no-cache mariadb
docker compose down
rm -rf db_data/
```

### 3. Start

```bash
docker compose up -d
```

### 4. Attach (for interactive console commands)

```bash
docker attach sectorized-mindustry
```

Press `Ctrl+P Ctrl+Q` to detach without stopping the container.

---

## Logs

```bash
# Last 1000 lines, follow new output
docker logs --tail 1000 --follow sectorized-mindustry

# MariaDB logs (useful for DB issues)
docker logs sectorized-mariadb

# Check if a specific block ran
docker logs sectorized-mariadb 2>&1 | grep -i "init\|error\|denied"
```

---

## Database management

### Manually create the ranking table

```bash
docker compose exec mariadb mariadb -u root -p"${MARIADB_ROOT_PASSWORD}" sectorized -e "
CREATE TABLE IF NOT EXISTS ranking (
    uuid VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    score INT DEFAULT 0,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    discordTag VARCHAR(255),
    empty INT DEFAULT 0
);
"
```

### Connect to the database

```bash
docker compose exec mariadb mariadb -u root -p"${MARIADB_ROOT_PASSWORD}" sectorized
```
