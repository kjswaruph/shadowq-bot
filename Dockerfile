FROM eclipse-temurin:21-jdk AS build
RUN apt-get update && \
    apt-get install -y wget unzip
WORKDIR /opt
RUN wget https://services.gradle.org/distributions/gradle-8.12-bin.zip && \
    unzip gradle-8.12-bin.zip
# Set environment variables for Gradle
ENV GRADLE_HOME=/opt/gradle-8.12
ENV PATH=$GRADLE_HOME/bin:$PATH

COPY --chown=gradle:gradle . /bot

WORKDIR /bot
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre AS run
RUN apt-get update && apt-get install -y graphviz
WORKDIR /bot
COPY --link --from=build /bot/build/libs/rooktownbot.jar ./rooktownbot.jar
ENTRYPOINT [ "java", "-jar", "rooktownbot.jar" ]

