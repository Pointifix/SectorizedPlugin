# Sectorized Plugin

A **PvP / Faction‑based game mode** for Mindustry. Players join factions, capture sectors, build cores, and fight for dominance on a procedurally generated map with biomes.

## Join the Server

```
sectorized.freeddns.org:6567
```

## Join our Discord

![Discord](https://img.shields.io/discord/945026790861176932.svg?logo=discord&logoColor=white&logoWidth=20&labelColor=7289DA&label=Discord&color=17cf48)

## Screenshot

<img width="918" height="917" alt="image" src="https://github.com/user-attachments/assets/76e8180c-a715-4c2d-af6d-1abadba79797" />

## Setup

See [DEPLOY.md](DEPLOY.md) for the full Docker deployment guide.

## Configuration

All config files live under `config/mods/config/` on the server:

| File | Purpose |
|------|---------|
| `sectorized-database-config.json` | MariaDB connection (`url`, `user`, `password`) |
| `sectorized-discord-config.json` | Discord bot token and channel IDs |
| `sectorized-game-config.json` | Game rules, multipliers, scoring, loadouts |

The `.env` file in the deploy directory sets MariaDB credentials (`MARIADB_USER`, `MARIADB_PASSWORD`, etc.) and must match the database config.

For questions or help configuring the plugin, contact us on Discord.
