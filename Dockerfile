FROM gradle:8-jdk21-alpine AS builder
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle shadowJar --no-daemon


FROM eclipse-temurin:21-jre AS run
WORKDIR /app
RUN apt-get update && apt-get install -y graphviz
COPY --from=builder /app/build/libs/rooktownbot.jar .
ENTRYPOINT ["java", "-jar", "rooktownbot.jar"]
