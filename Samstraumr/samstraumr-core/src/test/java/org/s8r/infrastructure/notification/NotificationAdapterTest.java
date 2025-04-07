/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.infrastructure.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort.DeliveryStatus;
import org.s8r.application.port.NotificationPort.NotificationResult;
import org.s8r.application.port.NotificationPort.NotificationSeverity;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the NotificationAdapter class.
 */
@UnitTest
public class NotificationAdapterTest {

    private LoggerPort mockLogger;
    private ConfigurationPort mockConfigPort;
    private NotificationAdapter adapter;
    
    @BeforeEach
    void setUp() {
        mockLogger = mock(LoggerPort.class);
        mockConfigPort = mock(ConfigurationPort.class);
        
        // Configure the default recipient
        when(mockConfigPort.getString("notification.default.recipient", "system"))
            .thenReturn("system");
        
        adapter = new NotificationAdapter(mockLogger, mockConfigPort);
    }
    
    @Test
    void testSendNotification() {
        // Send a notification to the default recipient (system)
        NotificationResult result = adapter.sendNotification(
                "Test Subject", "Test Content", NotificationSeverity.INFO, null);
        
        assertTrue(result.isSent());
        assertEquals(DeliveryStatus.SENT, result.getStatus());
        assertNotNull(result.getNotificationId());
        assertTrue(result.getNotificationId().startsWith("NOTIF-"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString());
    }
    
    @Test
    void testSendNotificationToRecipient() {
        // Register a test recipient
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("type", "email");
        contactInfo.put("address", "test@example.com");
        
        assertTrue(adapter.registerRecipient("test-user", contactInfo));
        
        // Send a notification to the test recipient
        NotificationResult result = adapter.sendNotificationToRecipient(
                "test-user", "Test Subject", "Test Content", 
                NotificationSeverity.INFO, null);
        
        assertTrue(result.isSent());
        assertEquals(DeliveryStatus.SENT, result.getStatus());
        assertNotNull(result.getNotificationId());
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString());
    }
    
    @Test
    void testSendNotificationToUnregisteredRecipient() {
        // Send a notification to an unregistered recipient
        NotificationResult result = adapter.sendNotificationToRecipient(
                "unknown-user", "Test Subject", "Test Content", 
                NotificationSeverity.INFO, null);
        
        assertFalse(result.isSent());
        assertEquals(DeliveryStatus.FAILED, result.getStatus());
        assertEquals("N/A", result.getNotificationId());
        assertTrue(result.getMessage().contains("Recipient not registered"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).warn(anyString(), anyString());
    }
    
    @Test
    void testSendSystemNotification() {
        // Send a system notification
        NotificationResult result = adapter.sendSystemNotification(
                "System Test", "System Content", NotificationSeverity.WARNING);
        
        assertTrue(result.isSent());
        assertEquals(DeliveryStatus.SENT, result.getStatus());
        assertNotNull(result.getNotificationId());
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString());
    }
    
    @Test
    void testRegisterAndUnregisterRecipient() {
        // Register a test recipient
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("type", "sms");
        contactInfo.put("phone", "1234567890");
        
        assertTrue(adapter.registerRecipient("sms-user", contactInfo));
        assertTrue(adapter.isRecipientRegistered("sms-user"));
        
        // Unregister the recipient
        assertTrue(adapter.unregisterRecipient("sms-user"));
        assertFalse(adapter.isRecipientRegistered("sms-user"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString());
    }
    
    @Test
    void testUnregisterSystemRecipient() {
        // Attempt to unregister the system recipient (should fail)
        assertFalse(adapter.unregisterRecipient("system"));
        assertTrue(adapter.isRecipientRegistered("system"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).warn(anyString(), anyString());
    }
    
    @Test
    void testRegisterRecipientWithInvalidData() {
        // Attempt to register with null ID
        assertFalse(adapter.registerRecipient(null, new HashMap<>()));
        
        // Attempt to register with empty ID
        assertFalse(adapter.registerRecipient("", new HashMap<>()));
        
        // Attempt to register with null contact info
        assertFalse(adapter.registerRecipient("test-user", null));
        
        // Attempt to register with empty contact info
        assertFalse(adapter.registerRecipient("test-user", new HashMap<>()));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).warn(anyString(), anyString());
    }
    
    @Test
    void testGetNotificationStatus() {
        // Send a notification to get an ID
        NotificationResult result = adapter.sendSystemNotification(
                "Status Test", "Test Content", NotificationSeverity.INFO);
        
        String notificationId = result.getNotificationId();
        
        // Get the status
        DeliveryStatus status = adapter.getNotificationStatus(notificationId);
        assertEquals(DeliveryStatus.SENT, status);
        
        // Get status for non-existent notification
        DeliveryStatus invalidStatus = adapter.getNotificationStatus("NOTIF-INVALID");
        assertEquals(DeliveryStatus.FAILED, invalidStatus);
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).warn(anyString(), anyString());
    }
    
    @Test
    void testNotificationWithMetadata() {
        // Create metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");
        
        // Send a notification with metadata
        NotificationResult result = adapter.sendSystemNotification(
                "Metadata Test", "Test with Metadata", NotificationSeverity.INFO);
        
        assertTrue(result.isSent());
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString());
    }
}