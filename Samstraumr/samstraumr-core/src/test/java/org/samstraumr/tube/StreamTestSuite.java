package org.samstraumr.tube;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.samstraumr.tube.bundle.Bundle;
import org.samstraumr.tube.test.annotations.StreamTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Stream Tests focus on external system interactions. These tests verify tube behavior when
 * interacting with external resources using TestContainers to provide isolated test environments.
 */
@StreamTest
@Testcontainers
public class StreamTestSuite {

  private static final Logger LOGGER = LoggerFactory.getLogger(StreamTestSuite.class);
  private static final String DB_NAME = "streamtest";
  private static final String DB_USERNAME = "tester";
  private static final String DB_PASSWORD = "testpassword";

  /**
   * PostgreSQL container for database tests. This container is shared across all tests in this
   * class for efficiency.
   */
  @Container
  private static final PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:14")
          .withDatabaseName(DB_NAME)
          .withUsername(DB_USERNAME)
          .withPassword(DB_PASSWORD)
          .withInitScript("streamtest/init.sql");

  private Environment environment;
  private Bundle bundle;
  private Connection dbConnection;

  @BeforeAll
  static void setupAll() {
    postgresContainer.start();
    LOGGER.info("Started PostgreSQL container on port {}", postgresContainer.getFirstMappedPort());
  }

  @AfterAll
  static void teardownAll() {
    if (postgresContainer.isRunning()) {
      postgresContainer.stop();
      LOGGER.info("Stopped PostgreSQL container");
    }
  }

  @BeforeEach
  void setUp() throws SQLException {
    environment = new Environment();
    bundle = new Bundle("stream-test-bundle", environment);

    // Create tubes for processing
    bundle
        .createTube("source", "Data Source Tube")
        .createTube("processor", "Data Processor Tube")
        .createTube("sink", "Database Sink Tube");

    // Connect tubes in processing pipeline
    bundle.connect("source", "processor").connect("processor", "sink");

    // Add data processors
    bundle.addTransformer("processor", (Function<String, String>) s -> s.toUpperCase());

    // Set up database connection
    dbConnection =
        DriverManager.getConnection(
            postgresContainer.getJdbcUrl(),
            postgresContainer.getUsername(),
            postgresContainer.getPassword());
  }

  @Test
  @DisplayName("Tube should write processed data to database")
  void shouldWriteProcessedDataToDatabase() throws SQLException {
    // Given
    String inputData = "test message";

    // Set up database sink
    bundle.addTransformer(
        "sink",
        (Function<String, String>)
            data -> {
              try {
                // Write data to database
                writeToDatabase(data);
                return data;
              } catch (SQLException e) {
                throw new RuntimeException("Database error: " + e.getMessage(), e);
              }
            });

    // When
    Optional<String> result = bundle.process("source", inputData);

    // Then
    assertTrue(result.isPresent());
    assertEquals("TEST MESSAGE", result.get());

    // Verify data was written to database
    assertTrue(verifyDataInDatabase("TEST MESSAGE"));
  }

  @Test
  @DisplayName("Tube should handle database connection failure")
  void shouldHandleDatabaseConnectionFailure() throws SQLException {
    // Given
    String inputData = "test message";

    // Close connection to simulate failure
    dbConnection.close();

    // Set up database sink with circuit breaker
    bundle.enableCircuitBreaker("sink", 2, 1000);
    bundle.addTransformer(
        "sink",
        (Function<String, String>)
            data -> {
              try {
                // Try to write data to database
                writeToDatabase(data);
                return data;
              } catch (SQLException e) {
                throw new RuntimeException("Database error: " + e.getMessage(), e);
              }
            });

    // When
    Optional<String> result = bundle.process("source", inputData);

    // Then
    assertFalse(result.isPresent()); // Should fail

    // Verify circuit breaker opened after failure
    Map<String, org.samstraumr.tube.composite.Composite.CircuitBreaker> breakers =
        bundle.getCircuitBreakers();
    assertNotNull(breakers.get("sink"));
    assertTrue(breakers.get("sink").isOpen());
  }

  // Helper methods

  private void writeToDatabase(String data) throws SQLException {
    if (dbConnection == null || dbConnection.isClosed()) {
      dbConnection =
          DriverManager.getConnection(
              postgresContainer.getJdbcUrl(),
              postgresContainer.getUsername(),
              postgresContainer.getPassword());
    }

    try (Statement stmt = dbConnection.createStatement()) {
      String sql =
          String.format(
              "INSERT INTO messages (content, created_at) VALUES ('%s', NOW())",
              data.replace("'", "''") // Escape single quotes
              );
      stmt.executeUpdate(sql);
    }
  }

  private boolean verifyDataInDatabase(String data) throws SQLException {
    if (dbConnection == null || dbConnection.isClosed()) {
      dbConnection =
          DriverManager.getConnection(
              postgresContainer.getJdbcUrl(),
              postgresContainer.getUsername(),
              postgresContainer.getPassword());
    }

    try (Statement stmt = dbConnection.createStatement()) {
      String sql =
          String.format(
              "SELECT COUNT(*) FROM messages WHERE content = '%s'",
              data.replace("'", "''") // Escape single quotes
              );
      var rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
    }
    return false;
  }
}
