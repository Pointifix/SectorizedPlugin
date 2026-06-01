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
