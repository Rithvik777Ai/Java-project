# ==========================================
# Multi-stage Dockerfile for CuraTransit
# Java 17 + Spring Boot 3
# ==========================================

# Stage 1: Build application using Maven & Temurin JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy POM and sources
COPY pom.xml .
COPY src ./src

# Build production executable JAR
RUN mvn clean package -DskipTests

# Stage 2: Lightweight JRE 17 runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Security: run as non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy executable jar from builder
COPY --from=builder /workspace/target/curatransit-1.0.0.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

# Render exposes dynamic port via $PORT environment variable
ENV PORT=8080
EXPOSE 8080 10000

# Optimized memory settings for cloud container (Render free tier 512MB)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
