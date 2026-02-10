/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Tests for the notification support classes.
 */
class NotificationSupportTest {

    @Test
    @DisplayName("NotificationSeverity enum should contain expected values")
    void testNotificationSeverityEnum() {
        // Check that all expected values are present
        assertEquals(5, NotificationSeverity.values().length, "Should have 5 severity levels");
        
        assertNotNull(NotificationSeverity.INFO);
        assertNotNull(NotificationSeverity.WARNING);
        assertNotNull(NotificationSeverity.ERROR);
        assertNotNull(NotificationSeverity.CRITICAL);
        assertNotNull(NotificationSeverity.DEBUG);
        
        // Test enum conversion
        assertEquals(NotificationSeverity.INFO, NotificationSeverity.valueOf("INFO"));
        assertEquals(NotificationSeverity.WARNING, NotificationSeverity.valueOf("WARNING"));
        assertEquals(NotificationSeverity.ERROR, NotificationSeverity.valueOf("ERROR"));
        assertEquals(NotificationSeverity.CRITICAL, NotificationSeverity.valueOf("CRITICAL"));
        assertEquals(NotificationSeverity.DEBUG, NotificationSeverity.valueOf("DEBUG"));
    }
    
    @Test
    @DisplayName("NotificationChannel enum should contain expected values")
    void testNotificationChannelEnum() {
        // Check that all expected values are present
        assertEquals(9, NotificationChannel.values().length, "Should have 9 channels");
        
        assertNotNull(NotificationChannel.EMAIL);
        assertNotNull(NotificationChannel.SMS);
        assertNotNull(NotificationChannel.IN_APP);
        assertNotNull(NotificationChannel.PUSH);
        assertNotNull(NotificationChannel.DESKTOP);
        assertNotNull(NotificationChannel.CONSOLE);
        assertNotNull(NotificationChannel.SLACK);
        assertNotNull(NotificationChannel.TEAMS);
        assertNotNull(NotificationChannel.WEB);
        
        // Test enum conversion
        assertEquals(NotificationChannel.EMAIL, NotificationChannel.valueOf("EMAIL"));
        assertEquals(NotificationChannel.SMS, NotificationChannel.valueOf("SMS"));
        assertEquals(NotificationChannel.IN_APP, NotificationChannel.valueOf("IN_APP"));
        assertEquals(NotificationChannel.PUSH, NotificationChannel.valueOf("PUSH"));
        assertEquals(NotificationChannel.DESKTOP, NotificationChannel.valueOf("DESKTOP"));
        assertEquals(NotificationChannel.CONSOLE, NotificationChannel.valueOf("CONSOLE"));
        assertEquals(NotificationChannel.SLACK, NotificationChannel.valueOf("SLACK"));
        assertEquals(NotificationChannel.TEAMS, NotificationChannel.valueOf("TEAMS"));
        assertEquals(NotificationChannel.WEB, NotificationChannel.valueOf("WEB"));
    }
    
    @Test
    @DisplayName("ContentFormat enum should contain expected values")
    void testContentFormatEnum() {
        // Check that all expected values are present
        assertEquals(5, ContentFormat.values().length, "Should have 5 content formats");
        
        assertNotNull(ContentFormat.TEXT);
        assertNotNull(ContentFormat.HTML);
        assertNotNull(ContentFormat.MARKDOWN);
        assertNotNull(ContentFormat.JSON);
        assertNotNull(ContentFormat.XML);
        
        // Test enum conversion
        assertEquals(ContentFormat.TEXT, ContentFormat.valueOf("TEXT"));
        assertEquals(ContentFormat.HTML, ContentFormat.valueOf("HTML"));
        assertEquals(ContentFormat.MARKDOWN, ContentFormat.valueOf("MARKDOWN"));
        assertEquals(ContentFormat.JSON, ContentFormat.valueOf("JSON"));
        assertEquals(ContentFormat.XML, ContentFormat.valueOf("XML"));
    }
    
    @Test
    @DisplayName("DeliveryStatus enum should contain expected values")
    void testDeliveryStatusEnum() {
        // Check that all expected values are present
        assertEquals(6, DeliveryStatus.values().length, "Should have 6 delivery statuses");
        
        assertNotNull(DeliveryStatus.PENDING);
        assertNotNull(DeliveryStatus.DELIVERED);
        assertNotNull(DeliveryStatus.FAILED);
        assertNotNull(DeliveryStatus.QUEUED);
        assertNotNull(DeliveryStatus.REJECTED);
        assertNotNull(DeliveryStatus.UNKNOWN);
        
        // Test enum conversion
        assertEquals(DeliveryStatus.PENDING, DeliveryStatus.valueOf("PENDING"));
        assertEquals(DeliveryStatus.DELIVERED, DeliveryStatus.valueOf("DELIVERED"));
        assertEquals(DeliveryStatus.FAILED, DeliveryStatus.valueOf("FAILED"));
        assertEquals(DeliveryStatus.QUEUED, DeliveryStatus.valueOf("QUEUED"));
        assertEquals(DeliveryStatus.REJECTED, DeliveryStatus.valueOf("REJECTED"));
        assertEquals(DeliveryStatus.UNKNOWN, DeliveryStatus.valueOf("UNKNOWN"));
    }
    
    @Test
    @DisplayName("NotificationResult should create successful result")
    void testNotificationResultSuccess() {
        // Test simple success case
        NotificationResult result = NotificationResult.success("Notification sent");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getNotificationId());
        assertEquals(DeliveryStatus.DELIVERED, result.getStatus());
        assertEquals("Notification sent", result.getMessage());
        assertFalse(result.getErrorDetails().isPresent());
        assertNotNull(result.getTimestamp());
        assertTrue(result.getMetadata().isEmpty());
        
        // Test success with metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("recipient", "user@example.com");
        metadata.put("channel", "EMAIL");
        
        NotificationResult resultWithMetadata = NotificationResult.success(
                "Notification sent with metadata", 
                DeliveryStatus.DELIVERED,
                metadata
        );
        
        assertTrue(resultWithMetadata.isSuccess());
        assertNotNull(resultWithMetadata.getNotificationId());
        assertEquals(DeliveryStatus.DELIVERED, resultWithMetadata.getStatus());
        assertEquals("Notification sent with metadata", resultWithMetadata.getMessage());
        assertFalse(resultWithMetadata.getErrorDetails().isPresent());
        assertNotNull(resultWithMetadata.getTimestamp());
        assertEquals(2, resultWithMetadata.getMetadata().size());
        assertEquals("user@example.com", resultWithMetadata.getMetadata().get("recipient"));
        assertEquals("EMAIL", resultWithMetadata.getMetadata().get("channel"));
    }
    
    @Test
    @DisplayName("NotificationResult should create failure result")
    void testNotificationResultFailure() {
        // Test simple failure case
        NotificationResult result = NotificationResult.failure(
                "Notification failed", 
                "Invalid recipient email"
        );
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getNotificationId());
        assertEquals(DeliveryStatus.FAILED, result.getStatus());
        assertEquals("Notification failed", result.getMessage());
        assertTrue(result.getErrorDetails().isPresent());
        assertEquals("Invalid recipient email", result.getErrorDetails().get());
        assertNotNull(result.getTimestamp());
        assertTrue(result.getMetadata().isEmpty());
        
        // Test failure with metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("recipient", "invalid-email");
        metadata.put("attempts", "3");
        
        NotificationResult resultWithMetadata = NotificationResult.failure(
                "Notification failed with metadata", 
                "Maximum retry attempts exceeded",
                DeliveryStatus.FAILED,
                metadata
        );
        
        assertFalse(resultWithMetadata.isSuccess());
        assertNotNull(resultWithMetadata.getNotificationId());
        assertEquals(DeliveryStatus.FAILED, resultWithMetadata.getStatus());
        assertEquals("Notification failed with metadata", resultWithMetadata.getMessage());
        assertTrue(resultWithMetadata.getErrorDetails().isPresent());
        assertEquals("Maximum retry attempts exceeded", resultWithMetadata.getErrorDetails().get());
        assertNotNull(resultWithMetadata.getTimestamp());
        assertEquals(2, resultWithMetadata.getMetadata().size());
        assertEquals("invalid-email", resultWithMetadata.getMetadata().get("recipient"));
        assertEquals("3", resultWithMetadata.getMetadata().get("attempts"));
    }
}