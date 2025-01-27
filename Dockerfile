FROM eclipse-temurin:21-jdk AS build
# Install dependencies
RUN apt-get update && \
    apt-get install -y wget unzip
# Download Gradle 8.12
WORKDIR /opt
RUN wget https://services.gradle.org/distributions/gradle-8.12-bin.zip && \
    unzip gradle-8.12-bin.zip
# Set environment variables for Gradle
COPY --chown=gradle:gradle . /bot

WORKDIR /bot
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre AS run
RUN apt-get update && apt-get install -y graphviz
WORKDIR /bot
COPY --link --from=build /bot/build/libs/rooktownbot.jar ./rooktownbot.jar
VOLUME ["/bot/db", "/bot/images/leaderboard", "/bot/images/scoreboard"]
ENTRYPOINT [ "java", "-jar", "rooktownbot.jar" ]

