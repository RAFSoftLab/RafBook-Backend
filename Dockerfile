# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Make the mvnw script executable
RUN chmod +x mvnw

# Package the application
RUN ./mvnw package

# Expose the port the application runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/RAFBook-0.1.jar"]