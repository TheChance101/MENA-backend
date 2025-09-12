FROM eclipse-temurin:17-jdk as builder

WORKDIR /app

# Copy only necessary files first for better caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies
RUN ./gradlew build --no-daemon --stacktrace -x test || return 0

# Copy the rest of the source code
COPY . .

# Ensure gradlew is executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew :app:bootJar --no-daemon

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the generated JAR from the builder image
COPY --from=builder app/app/build/libs/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
