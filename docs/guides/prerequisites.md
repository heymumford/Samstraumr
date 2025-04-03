# Samstraumr Prerequisites Guide

*Last Updated: April 3, 2025*

This guide details all prerequisites and setup requirements for developing with the Samstraumr framework.

## Table of Contents

- [System Requirements](#system-requirements)
- [Development Environment](#development-environment)
- [Required Software](#required-software)
- [Optional Tools](#optional-tools)
- [Environment Setup](#environment-setup)
- [IDE Configuration](#ide-configuration)
- [Troubleshooting](#troubleshooting)

## System Requirements

### Hardware Recommendations

- **Processor**: Multi-core CPU (4+ cores recommended)
- **Memory**: 8GB RAM minimum, 16GB+ recommended
- **Disk Space**: 2GB for source code, dependencies, and build artifacts

### Operating System Support

- **Linux**: Ubuntu 20.04+, Fedora 35+, or other major distributions
- **macOS**: 12 (Monterey)+ with Homebrew
- **Windows**: Windows 10/11 with WSL2 recommended for Linux-like experience

## Development Environment

### Java Development Kit (JDK)

- **Version**: JDK 17 or newer required
- **Vendor**: OpenJDK recommended, but any compatible JDK is supported

#### JDK Installation

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Fedora/RHEL:**
```bash
sudo dnf install java-17-openjdk-devel
```

**macOS (using Homebrew):**
```bash
brew tap homebrew/cask-versions
brew install --cask temurin17
```

**Windows:**
Download from [Adoptium](https://adoptium.net/temurin/releases/?version=17)

### Maven Build System

- **Version**: Apache Maven 3.8.0 or newer
- **Configuration**: Ensure MAVEN_HOME is set in your environment

#### Maven Installation

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install maven
```

**Fedora/RHEL:**
```bash
sudo dnf install maven
```

**macOS (using Homebrew):**
```bash
brew install maven
```

**Windows:**
Download from [Maven website](https://maven.apache.org/download.cgi) and add to PATH

### Version Control System

- **Tool**: Git 2.25.0 or newer
- **LFS Support**: Git LFS for handling large files (optional)

#### Git Installation

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install git git-lfs
```

**Fedora/RHEL:**
```bash
sudo dnf install git git-lfs
```

**macOS (using Homebrew):**
```bash
brew install git git-lfs
```

**Windows:**
Download from [Git website](https://git-scm.com/download/win)

## Required Software

### Docker/Podman (for TestContainers)

Testcontainers is used for integration tests and requires either Docker or Podman.

#### Docker Installation

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install docker.io
sudo systemctl enable --now docker
sudo usermod -aG docker $USER  # Log out and in again after this
```

**Fedora/RHEL:**
```bash
sudo dnf install docker
sudo systemctl enable --now docker
sudo usermod -aG docker $USER  # Log out and in again after this
```

**macOS:**
Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)

**Windows:**
Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)

#### Podman Installation (Alternative to Docker)

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install podman
```

**Fedora/RHEL:**
```bash
sudo dnf install podman
```

## Optional Tools

### Act - Local GitHub Actions Runner

[Act](https://github.com/nektos/act) allows running GitHub Actions workflows locally.

#### Act Installation

**All Linux/macOS:**
```bash
curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash
# Alternatively, install to /usr/local/bin
curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash -s -- -b /usr/local/bin
```

**macOS (using Homebrew):**
```bash
brew install act
```

**Windows:**
```bash
choco install act-cli
```

#### Act Configuration

Create the configuration file:
```bash
mkdir -p ~/.config/act
echo "-P ubuntu-latest=ghcr.io/catthehacker/ubuntu:act-latest" > ~/.config/act/actrc
```

### SonarQube Scanner

For code quality analysis with SonarQube.

#### SonarQube Scanner Installation

**All platforms:**
Download from [SonarQube website](https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner/)

**Linux/macOS (using Homebrew):**
```bash
brew install sonar-scanner
```

## Environment Setup

### Environment Variables

Add these to your shell profile (.bashrc, .zshrc, etc.):

```bash
# Java
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH

# Maven optimizations
export MAVEN_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Encoding settings
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
```

### Maven Settings

For faster builds, optimize Maven settings in `~/.m2/settings.xml`:

```xml
<settings>
  <mirrors>
    <mirror>
      <id>maven-default-http-blocker</id>
      <mirrorOf>external:http:*</mirrorOf>
      <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
      <url>http://0.0.0.0/</url>
      <blocked>true</blocked>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <id>samstraumr-defaults</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
      </properties>
    </profile>
  </profiles>
</settings>
```

## IDE Configuration

### Recommended IDEs

- **IntelliJ IDEA**: Community or Ultimate edition
- **Eclipse**: With Java and Maven plugins
- **VS Code**: With Java extension pack

### IntelliJ IDEA Configuration

1. Install the Cucumber for Java plugin
2. Import the project as a Maven project
3. Configure code style to use the EditorConfig settings
4. Set project JDK to Java 17+
5. Enable annotation processing

### Eclipse Configuration

1. Install Cucumber Eclipse Plugin
2. Import as Maven project
3. Install EditorConfig plugin
4. Set compiler compliance level to 17

### VS Code Configuration

1. Install Java Extension Pack
2. Install Cucumber (Gherkin) Support extension
3. Install EditorConfig extension
4. Set Java version to 17 in settings

## Troubleshooting

### Common Issues

#### Maven Build Failures

**Issue**: Maven cannot download dependencies
**Solution**: Check network connectivity and proxy settings in ~/.m2/settings.xml

**Issue**: Out of memory during build
**Solution**: Increase memory in MAVEN_OPTS

#### Docker/Podman Access Issues

**Issue**: Permission denied accessing the Docker socket
**Solution**: Add your user to the docker group and restart your session

**Issue**: Act cannot connect to Docker
**Solution**: Run Act with sudo or ensure Docker is properly installed

#### Java Version Mismatch

**Issue**: Incompatible class version error
**Solution**: Verify you're using Java 17 or newer for both IDE and command line

```bash
java -version
javac -version
```

### Getting Help

If you encounter issues not covered here:

1. Check the [FAQ](../reference/faq.md) document
2. Open an issue on the GitHub repository
3. Contact the maintainers via the project's communication channels