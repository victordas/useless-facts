# Stage 1: Build the application using Gradle
FROM gradle:8.5-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle build files
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./

# Copy Kotlin source code
COPY src src

# Build the Kotlin Spring Boot app
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Run the application in a minimal image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 4400

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
