FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

# Copy the project files
COPY . .

# Build the Gradle project
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

# Create a new image for the final container
FROM eclipse-temurin:21-jre-alpine

# Set the working directory for the final image
WORKDIR /app

# Copy the built JAR file from the builder container
COPY --from=builder /app/build/libs/chatAssistantBackend-1.0.0-SNAPSHOT.jar /app/chatAssistantBackend.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "chatAssistantBackend.jar"]