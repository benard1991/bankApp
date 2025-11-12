# ---- BUILD STAGE ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the JAR (skip tests)
RUN mvn clean package -Dmaven.test.skip=true

# ---- RUNTIME STAGE ----
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set default JVM options (optional but recommended)
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
