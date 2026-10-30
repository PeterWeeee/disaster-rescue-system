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
ENV TZ=Asia/Ho_Chi_Minh
EXPOSE 8080

# Use SerialGC, limit heap to 350MB, and enforce Vietnam Timezone (GMT+7)
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-XX:+UseSerialGC", "-Duser.timezone=Asia/Ho_Chi_Minh", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
