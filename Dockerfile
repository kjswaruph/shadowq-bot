FROM gradle:8-jdk21-alpine AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle shadowJar --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache sqlite graphviz
COPY --from=builder /app/build/libs/rooktownbot.jar .
COPY .env .
RUN mkdir -p \
    /data/db \
    /data/leaderboards \
    /data/scoreboards
VOLUME ["/data/db", "/data/leaderboards", "/data/scoreboards"]
CMD ["java", "-jar", "rooktownbot.jar"]
