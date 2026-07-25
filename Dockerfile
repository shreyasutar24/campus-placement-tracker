# Step 1: Build JAR
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run JAR
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]