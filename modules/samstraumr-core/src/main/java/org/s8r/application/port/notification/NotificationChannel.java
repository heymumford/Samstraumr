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

/** Enumeration of notification channels. */
public enum NotificationChannel {
  /** Email notification channel. */
  EMAIL,

  /** SMS notification channel. */
  SMS,

  /** In-app notification channel. */
  IN_APP,

  /** Push notification channel. */
  PUSH,

  /** Desktop notification channel. */
  DESKTOP,

  /** Console notification channel. */
  CONSOLE,

  /** Slack notification channel. */
  SLACK,

  /** Teams notification channel. */
  TEAMS,

  /** Web notification channel. */
  WEB
}
