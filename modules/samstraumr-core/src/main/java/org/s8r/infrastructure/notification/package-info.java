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

/**
 * Notification infrastructure implementations.
 *
 * <p>This package contains adapter implementations of the notification port interface for the
 * Samstraumr system. The adapters provide the actual implementations of the notification mechanisms
 * used by the application.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>In-memory notification adapter for development and testing
 *   <li>Support for multiple notification channels (email, SMS, push)
 *   <li>Notification delivery tracking
 *   <li>Recipient management
 *   <li>Simulation of delivery failures for testing
 * </ul>
 *
 * <p>The NotificationAdapter class implements the NotificationPort interface from the application
 * layer, providing a bridge between the application's notification requirements and the actual
 * notification delivery mechanisms. This follows the Ports and Adapters pattern (Hexagonal
 * Architecture) where the application core defines the port interfaces, and the infrastructure
 * layer provides concrete adapter implementations.
 *
 * <p>Production systems would typically provide additional concrete adapters that connect to actual
 * email, SMS, and push notification services (e.g., via SMTP, Twilio, Firebase Cloud Messaging,
 * etc.).
 */
package org.s8r.infrastructure.notification;
