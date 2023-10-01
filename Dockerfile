# Use the official Java 17 image as the base image
FROM openjdk:17-slim

# Set the working directory in the container to /app
WORKDIR /app

# Copy the jar file from your target folder to the present location (/app) inside the container
COPY ./target/ecommerce-crud-api-*.jar ecommerce-crud-api.jar

# Set the startup command to run the jar
CMD ["java", "-jar", "ecommerce-crud-api.jar"]