FROM openjdk:21

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot app will run on
EXPOSE 8080

# Specify the command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]