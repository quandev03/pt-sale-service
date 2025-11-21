# syntax=docker/dockerfile:1.6

ARG MAVEN_IMAGE=maven:3.9-eclipse-temurin-17
ARG RUNTIME_IMAGE=eclipse-temurin:17-jre

# ===== STAGE 1: BUILD =====
FROM ${MAVEN_IMAGE} AS build

WORKDIR /workspace

# Copy Maven settings & project metadata first to maximize layer caching
COPY settings-vnsky.xml /tmp/settings.xml
COPY pom.xml .
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY .mvn .mvn

# Pre-download dependencies using BuildKit cache
RUN --mount=type=cache,target=/root/.m2 \
    mvn -s /tmp/settings.xml -B -e -U -DskipTests dependency:go-offline

# Copy sources only after dependencies are cached
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -s /tmp/settings.xml -B -e -DskipTests package

# ===== STAGE 2: RUNTIME =====
FROM ${RUNTIME_IMAGE} AS runtime
WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

ENV TZ=Asia/Ho_Chi_Minh

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]