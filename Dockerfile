# Multi-stage build for Spring Boot 3 with Java 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace/app

# Limit Maven memory so it never exceeds Render Free Tier (512MB RAM)
ENV MAVEN_OPTS="-Xmx384m -XX:+UseSerialGC"

COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests -B

# Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /workspace/app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Use SerialGC and limit heap to 350MB to fit within 512MB container
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-XX:+UseSerialGC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
