#!/bin/sh

rm -f /tmp/mindustry.fifo
mkfifo /tmp/mindustry.fifo

while true; do
    echo "Starting Mindustry Server..."

    exec 3<>/tmp/mindustry.fifo

    java -jar /server/server.jar < /tmp/mindustry.fifo &
    JAVA_PID=$!

    (
        sleep 1
        echo "config name [purple]S[magenta]E[red]C[orange]T[yellow]O[green]R[cyan]I[blue]Z[purple]E[red]D"
        echo "sectorized"
    ) >&3

    cat >&3 &
    CAT_PID=$!

    wait $JAVA_PID

    # Clean up the stdin forwarder
    kill $CAT_PID 2>/dev/null
    wait $CAT_PID 2>/dev/null
    exec 3>&-

    echo "Server stopped. Cleaning up..."
    sleep 1
done