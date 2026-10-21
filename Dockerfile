# Multi-stage build for College Communication Portal

# Stage 1: Build the WAR file using Maven and JDK 17
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the production WAR package (skipping tests)
RUN mvn clean package -DskipTests

# Stage 2: Run Tomcat 10 on lightweight JDK 17
FROM tomcat:10.1-jdk17-temurin-jammy

# Remove default Tomcat root apps to avoid conflicts
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR as ROOT.war so portal opens directly at the root URL (e.g. https://my-app.onrender.com/)
COPY --from=builder /app/target/college-portal.war /usr/local/tomcat/webapps/ROOT.war

# Create upload directory in container home directory
RUN mkdir -p /root/college_portal_uploads

# Expose Tomcat default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
