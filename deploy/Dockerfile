FROM eclipse-temurin:17-alpine
RUN apk add --no-cache curl
WORKDIR /server
RUN curl -L -o server.jar https://github.com/Anuken/Mindustry/releases/download/v158.1/server-release.jar
EXPOSE 6567/tcp 6567/udp
CMD sh -c "(sleep 3 && echo 'config name [purple]S[magenta]E[red]C[orange]T[yellow]O[green]R[cyan]I[blue]Z[purple]E[red]D' && echo 'config motd FFA | PvP | Sector' && echo 'sectorized'; cat) | java -jar /server/server.jar"
