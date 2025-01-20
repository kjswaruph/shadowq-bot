FROM eclipse-temurin:21-jdk AS build

COPY --chown=gradle:gradle . /bot

WORKDIR /bot

RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre AS run

WORKDIR /bot

COPY --link --from=build /bot/build/libs/mybot.jar ./bot.jar

ENTRYPOINT [ "java", "-jar", "bot.jar" ]