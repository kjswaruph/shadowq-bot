FROM eclipse-temurin:21-jdk AS build
# Install dependencies
RUN apt-get update && \
    apt-get install -y wget unzip
# Download Gradle 8.12
WORKDIR /opt
RUN wget https://services.gradle.org/distributions/gradle-8.12-bin.zip && \
    unzip gradle-8.12-bin.zip
# Set environment variables for Gradle
ENV GRADLE_HOME=/opt/gradle-8.12
ENV PATH=$GRADLE_HOME/bin:$PATH

COPY --chown=gradle:gradle . /bot

WORKDIR /bot

RUN mkdir -p /bot/db /bot/images/leaderboard /bot/images/scoreboard
ENV BOT_DB_PATH=/bot/db
ENV BOT_LEADERBOARD_PATH=/bot/images/leaderboard
ENV BOT_SCOREBOARD_PATH=/bot/images/scoreboard

RUN gradle shadowJar --no-daemon
FROM eclipse-temurin:21-jre AS run
# Install Graphviz in the runtime container
RUN apt-get update && apt-get install -y graphviz
WORKDIR /bot
COPY --link --from=build /bot/build/libs/rooktownbot.jar ./rooktownbot.jar
VOLUME ["/bot/db", "/bot/images/leaderboard", "/bot/images/scoreboard"]
ENTRYPOINT [ "java", "-jar", "rooktownbot.jar" ]