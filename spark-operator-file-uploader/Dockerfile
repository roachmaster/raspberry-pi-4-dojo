# Use a base image with Java and Maven installed
FROM openjdk:11-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container at /app
COPY target/SparkOperatorFileUploader-1.0-SNAPSHOT.jar /app/app.jar

# Copy the application configuration file into the container at /app
COPY src/main/resources/application.properties /app/application.properties

# Expose the port that the application runs on
EXPOSE 8080

# Specify the command to run your application when the container starts
CMD ["java", "-jar", "app.jar"]
