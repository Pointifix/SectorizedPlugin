FROM eclipse-temurin:17-alpine
RUN apk add --no-cache curl
WORKDIR /server
RUN curl -L -o server.jar https://github.com/Anuken/Mindustry/releases/download/v158.1/server-release.jar
EXPOSE 6567/tcp 6567/udp

COPY start.sh /server/start.sh
RUN chmod +x /server/start.sh

CMD ["/bin/sh", "/server/start.sh"]
