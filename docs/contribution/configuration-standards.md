<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Configuration Standards

This document outlines best practices for handling configuration and avoiding hardcoded values in Samstraumr.

## General Principles

- **Externalize Configuration**: Configuration that might vary between environments should be externalized
- **Secure Credentials**: Never hardcode credentials or sensitive information
- **Environment Awareness**: Support different configurations for development, testing, and production
- **Sensible Defaults**: Provide reasonable defaults for all configurable parameters
- **Documentation**: Document all configuration options and their purpose

## Configuration Categories

### 1. application configuration

Application-level settings should be stored in properties files:

- **Core Settings**: `application.properties` or YAML for core application configuration
- **Environment-Specific**: Use `application-{env}.properties` for environment-specific overrides
- **Default Values**: Ensure sensible defaults are provided in code

```java
// Recommended pattern for configuration properties
@ConfigurationProperties(prefix = "samstraumr.circuit-breaker")
public class CircuitBreakerProperties {
    private int failureThreshold = 3; // Sensible default
    private long resetTimeoutMs = 5000; // Sensible default
    
    // Getters and setters
}
```

### 2. secure credentials

Sensitive information should NEVER be hardcoded:

- **Environment Variables**: Use for sensitive data in development
- **Secrets Management**: Use GitHub Secrets or equivalent for CI/CD pipelines
- **Vault Systems**: Consider HashiCorp Vault or similar for production
- **Test Credentials**: Store test credentials in separate properties files outside source control

Example pattern for accessing credentials:

```java
// Get from environment variable with fallback to properties
private String getDbPassword() {
    String envPassword = System.getenv("DB_PASSWORD");
    return envPassword != null ? envPassword : properties.getDbPassword();
}
```

### 3. business logic constants

Constants related to business logic should be centralized:

- **Constant Classes**: Group related constants in dedicated classes
- **Enum Types**: Use enums for related constants with behavior
- **Static Imports**: Use static imports for cleaner code

```java
// Example of constants class
public final class ValidationConstants {
    private ValidationConstants() {} // Prevent instantiation
    
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
}
```

### 4. resource locations

File paths and resource locations should be configurable:

- **Classpath Resources**: Use classpath references when possible
- **Configurable Paths**: Make external file paths configurable
- **Relative Paths**: Use relative paths where appropriate

```java
// Example of resource loading
public InputStream getResource(String resourceName) {
    return getClass().getClassLoader().getResourceAsStream(resourceName);
}
```

## Implementation Strategies

### Properties files

The primary way to configure Samstraumr is through properties files:

- Place in `src/main/resources` for application configuration
- Use standard Java Properties API or Spring's PropertySource
- Prefer structured formats like YAML for complex configuration

### Environment variables

Environment variables are ideal for:

- Credentials and secrets
- Environment-specific configuration
- Overriding default configuration

```
# Configuration Standards
export SAMSTRAUMR_LOG_LEVEL=INFO
export SAMSTRAUMR_DB_PASSWORD=securepassword
export SAMSTRAUMR_CIRCUIT_BREAKER_THRESHOLD=5
```

### Database configuration

For configuration that needs to be:
- Changed at runtime
- Modified by administrators
- Shared across instances

Consider storing in a database with a configuration service layer.

## Recommendations for Current Hardcoded Values

Based on analysis of the codebase, the following hardcoded values should be parameterized:

### Highest priority

1. **Circuit Breaker Parameters**
   - Currently: Hardcoded in `BundleFactory.java`
   - Recommendation: Move to configuration properties
2. **Log Configuration**
   - Currently: Hardcoded in `log4j2.xml`
   - Recommendation: Make log levels, paths, and retention configurable
3. **Test Database Credentials**
   - Currently: Hardcoded in `StreamTestSuite.java`
   - Recommendation: Move to test properties file

### Medium priority

1. **Bundle ID Generation**
   - Currently: Hardcoded timestamp-based IDs in `BundleFactory.java`
   - Recommendation: Create configurable ID generation strategy
2. **Email Validation Pattern**
   - Currently: Hardcoded in `AcceptanceTestSteps.java`
   - Recommendation: Move to ValidationConstants class

### Lower priority

1. **System Version in Tests**
   - Currently: Hardcoded in `AcceptanceTestSteps.java`
   - Recommendation: Derive from project version in pom.xml

## Code Review Checklist for Configuration

When reviewing code, check for:

- [ ] No hardcoded credentials or secrets
- [ ] No environment-specific configuration in code
- [ ] Reasonable defaults for all configurable parameters
- [ ] Documented configuration options
- [ ] Centralized access to configuration
