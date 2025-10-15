# 1. Use a Java base image to build and run the app
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# 2. Set the working directory
WORKDIR /app

# 3. Copy the Maven build descriptor and dependencies for faster builds
COPY pom.xml .
COPY lombok.config .

# 4. Copy the source code
COPY src ./src

# 5. Build the application with Maven
RUN mvn dependency:go-offline -B && mvn clean package -DskipTests

# 6. Runtime stage: use a lighter JRE image
FROM eclipse-temurin:21-jre

# 7. Set the working directory
WORKDIR /app

# 8. Copy the generated JAR from the builder image
COPY --from=builder /app/target/awesomepizza-*.jar app.jar

# 9. Expose the application port
EXPOSE 8080

# 10. Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
