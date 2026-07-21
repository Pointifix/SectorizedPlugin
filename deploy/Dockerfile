FROM eclipse-temurin:17-alpine
RUN apk add --no-cache curl
WORKDIR /server
RUN curl -L -o server.jar https://github.com/Anuken/Mindustry/releases/download/v159.7/server-release.jar
COPY start.sh /server/start.sh
RUN chmod +x /server/start.sh
EXPOSE 6567/tcp 6567/udp
CMD ["/server/start.sh"]
