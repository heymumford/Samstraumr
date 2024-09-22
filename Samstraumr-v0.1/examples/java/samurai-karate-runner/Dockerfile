# Karate Test Automation Platform Dockerfile
# Using Tube Based Design (TBD) Principles on Alpine Linux

# Tube Based Design Explanation:
# In this Dockerfile, we use the term "Tube" to represent modular, self-contained
# units of functionality. Each "Tube" is implemented as a stage in a multi-stage Docker build.
# This approach allows for better organization, maintainability, and potential reuse of build stages.

# Key TBD Principles Applied:
# 1. Modularity: Each Tube focuses on a specific set of related functionalities.
# 2. Encapsulation: Tubes manage their own dependencies and configurations.
# 3. Composability: Tubes build upon each other, creating a layered architecture.
# 4. Adaptability: Use of ARGs and ENVs allows for easy customization.
# 5. Interface Standardization: Consistent patterns across Tubes for installation and setup.

# Base Tube: Core Alpine Linux System
FROM alpine:3.20 AS base

# Set maintainer label
LABEL maintainer="Your Name <your.email@example.com>"

# Set environment variables
ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8

# Update Alpine and install basic utilities
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
    wget \
    curl \
    ca-certificates \
    bash  # Ensure bash is available

# Set working directory
WORKDIR /app

# Java Tube: OpenJDK 11 Environment
FROM base AS java

# Set Java version
ARG JAVA_VERSION=11

# Install OpenJDK 11
RUN apk add --no-cache openjdk${JAVA_VERSION} && \
    java -version

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk
ENV PATH=$PATH:$JAVA_HOME/bin

# Verify Java installation
RUN echo "Java version:" && java -version && \
    echo "Java home:" && echo $JAVA_HOME

# Maven Tube: Build and Dependency Management
FROM java AS maven

# Set Maven version
ARG MAVEN_VERSION=3.9.9

# Install Maven
RUN wget https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -zxvf apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    mv apache-maven-${MAVEN_VERSION} /usr/lib/mvn && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Set Maven environment variables
ENV MAVEN_HOME=/usr/lib/mvn
ENV PATH=$PATH:$MAVEN_HOME/bin

# Verify Maven installation
RUN mvn -version

# Karate Tube: Test Automation Platform
FROM maven AS karate

# Create a non-root user to run tests
RUN adduser -D karate
USER karate

# Set up a volume for test files
VOLUME /karate/tests

# Set working directory
WORKDIR /karate/tests

# Create a default entrypoint script
RUN echo '#!/bin/bash' > /karate/entrypoint.sh && \
    echo 'if [ -z "$1" ]; then exec /bin/bash; else mvn test -Dtest=Runner "$@"; fi' >> /karate/entrypoint.sh && \
    chmod +x /karate/entrypoint.sh

# Set the entrypoint
ENTRYPOINT ["/karate/entrypoint.sh"]

# Usage and Testing Instructions:
# 1. Build the Docker image:
#    docker build -t karate-test-env:latest .
#
# 2. Verify the image:
#    docker run --rm karate-test-env:latest java -version
#    docker run --rm karate-test-env:latest mvn -version
#
# 3. Run Karate tests:
#    docker run -v /path/to/your/tests:/karate/tests karate-test-env:latest
#
# 4. To run specific tests or pass additional Maven options:
#    docker run -v /path/to/your/tests:/karate/tests karate-test-env:latest -Dtest=SpecificTestRunner
#
# 5. To interact with the Docker container using a Bash shell:
#    docker run -it --rm karate-test-env:latest

# Maintenance Notes:
# - To update Java: Modify the JAVA_VERSION argument in the Java Tube
# - To update Maven: Modify the MAVEN_VERSION argument in the Maven Tube
# - For adding new dependencies, consider creating a new Tube or extending the Karate Tube

# Security Notes:
# - This image runs tests as a non-root user 'karate' for improved security
# - Regularly update the base Alpine image to get the latest security patches

# End of Dockerfile

