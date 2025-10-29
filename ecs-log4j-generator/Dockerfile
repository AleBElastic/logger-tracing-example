# Stage 1: Build Stage (using the most universal Java 17 JDK)
# We use a generic, multi-platform-friendly tag and install Maven inside it.
FROM eclipse-temurin:17-jdk-focal AS build

# Install Maven using the apt package manager
RUN apt-get update && apt-get install -y maven

# Set the working directory
WORKDIR /app
# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the fat JAR using the assembly goal
# This command runs your LogGenerator application's dependencies into one JAR
RUN mvn clean package

# Stage 2: Runtime Stage (Alpine JRE)
# Use a lean Alpine JRE image for the final runtime
# ... (Build Stage remains the same)

# Stage 2: Runtime Stage (Alpine JRE)
FROM eclipse-temurin:17-jdk-focal

# Set the application's environment
ENV JAVA_OPTS="-XX:+UseParallelGC -XX:MinRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0" 

# Copy the built SHADED JAR from the 'build' stage
# Note the change in the file name copied from the target folder!
COPY --from=build /app/target/ecs-log4j-generator-1.0.0.jar /app/app.jar

COPY --from=build /app/target/lib /app/lib


# Define the entrypoint
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]