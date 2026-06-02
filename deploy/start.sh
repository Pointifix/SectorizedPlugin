#!/bin/sh

rm -f /tmp/mindustry.fifo
mkfifo /tmp/mindustry.fifo

(
    sleep 5
    echo "config name [purple]S[magenta]E[red]C[orange]T[yellow]O[green]R[cyan]I[blue]Z[purple]E[red]D"
    echo "config motd FFA | PvP | Sector"
    echo "sectorized"
) > /tmp/mindustry.fifo &

echo "Server starting..."
cat /tmp/mindustry.fifo - | exec java -jar /server/server.jar
