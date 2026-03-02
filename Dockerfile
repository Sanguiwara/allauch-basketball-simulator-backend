# syntax=docker/dockerfile:1.6

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy Gradle wrapper + root build files first to leverage Docker layer caching
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# (Optionnel mais recommandé) Préparer le cache Gradle + vérifier le wrapper
RUN chmod +x ./gradlew

# Copy modules
COPY api ./api
COPY domain ./domain
COPY infra ./infra
COPY application-core ./application-core

# Build (cache Gradle via BuildKit)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :api:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copie du jar généré
COPY --from=build /workspace/api/build/libs/*.jar /app/app.jar

# Render sets PORT; fall back to 8080 for local runs
ENV JAVA_OPTS=""
EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
