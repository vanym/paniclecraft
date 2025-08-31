FROM openjdk:8u342-jdk-bullseye AS depser
COPY . /root/src
WORKDIR /root/src
SHELL ["/bin/bash", "-c"]
RUN ./gradlew jar --no-daemon && \
    ./gradlew eclipse --no-daemon && \
    ./gradlew idea --no-daemon && \
    shopt -s extglob && \
    eval 'rm -rf ~/.gradle/!("caches"|"wrapper")' && \
    shopt -u extglob && \
    find ~/.gradle -type f -name '*.lock' -print0 | xargs -r -0 rm -f && \
    rm -rf ~/.gradle/caches/minecraft/net/minecraftforge/forge/*/unpacked/ && \
    rm -rf ~/.gradle/caches/minecraft/net/minecraft/minecraft_merged/ && \
    rm -rf ~/.gradle/caches/jars-*

FROM openjdk:8u342-jdk-bullseye
COPY --from=depser /root/.gradle /root/.gradle
