# Stage 1: Build the Spring Boot JAR
FROM maven:3.9.6-ibm-semeru-21-jammy AS build
WORKDIR /app

RUN --mount=type=secret,id=SERVER_PORT \
    --mount=type=secret,id=POSTGRES_URL \
    --mount=type=secret,id=POSTGRES_PORT \
    --mount=type=secret,id=POSTGRES_DB \
    --mount=type=secret,id=POSTGRES_USER \
    --mount=type=secret,id=POSTGRES_PASSWORD \
    --mount=type=secret,id=HOST \
    --mount=type=secret,id=JWT_SECRET_KEY \
    --mount=type=secret,id=ACCOUNT_ACCESS_KEY_FROM_AWS \
    --mount=type=secret,id=ACCOUNT_SECRET_KEY_FROM_AWS \
    --mount=type=secret,id=S3_CONFIGURED_REGION_FROM_AWS \
    --mount=type=secret,id=S3_BUCKET_NAME \
    --mount=type=secret,id=S3_ENDPOINT \
    --mount=type=secret,id=MINIO_ACCESS_KEY \
    --mount=type=secret,id=MINIO_SECRET_KEY \
    --mount=type=secret,id=MINIO_BUCKET_NAME \
    --mount=type=secret,id=MINIO_ENDPOINT \
    export SERVER_PORT=$(cat /run/secrets/SERVER_PORT) && \
    export POSTGRES_URL=$(cat /run/secrets/POSTGRES_URL) && \
    export POSTGRES_PORT=$(cat /run/secrets/POSTGRES_PORT) && \
    export POSTGRES_DB=$(cat /run/secrets/POSTGRES_DB) && \
    export POSTGRES_USER=$(cat /run/secrets/POSTGRES_USER) && \
    export POSTGRES_PASSWORD=$(cat /run/secrets/POSTGRES_PASSWORD) && \
    export HOST=$(cat /run/secrets/HOST) && \
    export JWT_SECRET_KEY=$(cat /run/secrets/JWT_SECRET_KEY) && \
    export ACCOUNT_ACCESS_KEY_FROM_AWS=$(cat /run/secrets/ACCOUNT_ACCESS_KEY_FROM_AWS) && \
    export ACCOUNT_SECRET_KEY_FROM_AWS=$(cat /run/secrets/ACCOUNT_SECRET_KEY_FROM_AWS) && \
    export S3_CONFIGURED_REGION_FROM_AWS=$(cat /run/secrets/S3_CONFIGURED_REGION_FROM_AWS) && \
    export S3_BUCKET_NAME=$(cat /run/secrets/S3_BUCKET_NAME) && \
    export S3_ENDPOINT=$(cat /run/secrets/S3_ENDPOINT) && \
    export MINIO_ACCESS_KEY=$(cat /run/secrets/MINIO_ACCESS_KEY) && \
    export MINIO_SECRET_KEY=$(cat /run/secrets/MINIO_SECRET_KEY) && \
    export MINIO_BUCKET_NAME=$(cat /run/secrets/MINIO_BUCKET_NAME) && \
    export MINIO_ENDPOINT=$(cat /run/secrets/MINIO_ENDPOINT)

# Copy the project's POM file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the JAR
COPY src /app/src
RUN mvn clean verify -DskipTests

# Stage 2: Create the final image with the built JAR
FROM openjdk:21-slim

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/leasing.jar app.jar

# Expose the port that the Spring Boot app will run on
EXPOSE 8080

# Specify the command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]