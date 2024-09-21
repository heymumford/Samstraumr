# Start with an official Alpine base image
FROM alpine:latest AS base

# Install essential tools and dependencies
RUN apk add --no-cache \
    curl \
    bash \
    build-base \
    libffi-dev \
    openssl-dev \
    zlib-dev \
    musl-dev \
    linux-headers \
    python3-dev \
    py3-pip \
    pkgconfig \
    gcc \
    g++ \
    cairo-dev \
    pango-dev \
    gdk-pixbuf-dev \
    jpeg-dev \
    zlib-dev \
    tiff-dev \
    freetype-dev

# Install OpenJDK 21 using Eclipse Temurin
RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk add temurin-21-jdk


# Set JAVA_HOME for Temurin JDK
ENV JAVA_HOME=/usr/lib/jvm/jdk-21
ENV PATH=$JAVA_HOME/bin:$PATH


# -----------------------------------------
# Install Python 3 and Virtual Environment
# -----------------------------------------
RUN apk add --no-cache python3 py3-pip

# Create a Python virtual environment and install dependencies inside it
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --no-cache-dir pandas matplotlib pytest

# Ensure the virtual environment is activated by default
ENV PATH="/opt/venv/bin:$PATH"

# -----------------------------------------
# Install R and Dependencies
# -----------------------------------------
RUN apk add --no-cache R R-dev && \
    R -e "install.packages(c('ggplot2', 'forecast', 'testthat', 'dplyr'), repos='http://cran.us.r-project.org')"

# Remove unnecessary build dependencies to slim down the image
RUN apk del build-base linux-headers musl-dev libffi-dev openssl-dev zlib-dev

# Set working directory
WORKDIR /usr/src/app

# Copy over project files
COPY . .

# Command to run the Java app
CMD ["java", "-jar", "target/samstraumr-causality-engine.jar"]

