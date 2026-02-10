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

/** Enumeration of possible delivery statuses for notifications. */
public enum DeliveryStatus {
  /** The notification was delivered successfully. */
  DELIVERED,

  /** The notification delivery failed. */
  FAILED,

  /** The notification delivery is pending. */
  PENDING,

  /** The notification was queued for later delivery. */
  QUEUED,

  /** The notification was rejected by the delivery system. */
  REJECTED,

  /** The notification was sent (dispatch initiated but final delivery status unknown). */
  SENT,

  /** The notification delivery status is unknown. */
  UNKNOWN
}
