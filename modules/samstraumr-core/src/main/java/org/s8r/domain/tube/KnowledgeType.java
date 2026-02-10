/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.domain.tube;

/**
 * Represents a type of knowledge that can be accumulated and transferred by tubes during
 * reproduction.
 */
public class KnowledgeType {
  private final String name;
  private final String priority;
  private final String transferMethod;
  private boolean transferred;
  private boolean verified;

  /**
   * Creates a new knowledge type
   *
   * @param name The name of the knowledge type
   * @param priority The priority level ("high", "medium", "low")
   * @param transferMethod The method used to transfer this knowledge
   */
  public KnowledgeType(String name, String priority, String transferMethod) {
    this.name = name;
    this.priority = priority;
    this.transferMethod = transferMethod;
    this.transferred = false;
    this.verified = false;
  }

  /**
   * Gets the name of the knowledge type
   *
   * @return The name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the priority of the knowledge type
   *
   * @return The priority ("high", "medium", "low")
   */
  public String getPriority() {
    return priority;
  }

  /**
   * Gets the transfer method for this knowledge type
   *
   * @return The transfer method
   */
  public String getTransferMethod() {
    return transferMethod;
  }

  /**
   * Checks if this knowledge is considered high priority
   *
   * @return true if high priority, false otherwise
   */
  public boolean isHighPriority() {
    return "high".equalsIgnoreCase(priority);
  }

  /**
   * Checks if the knowledge has been transferred
   *
   * @return true if transferred, false otherwise
   */
  public boolean isTransferred() {
    return transferred;
  }

  /**
   * Sets the transferred status of this knowledge
   *
   * @param transferred The new transferred status
   */
  public void setTransferred(boolean transferred) {
    this.transferred = transferred;
  }

  /**
   * Checks if the knowledge transfer has been verified
   *
   * @return true if verified, false otherwise
   */
  public boolean isVerified() {
    return verified;
  }

  /**
   * Sets the verification status of this knowledge transfer
   *
   * @param verified The new verification status
   */
  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  /**
   * Gets the numerical priority value for sorting
   *
   * @return A numerical priority (3=high, 2=medium, 1=low)
   */
  public int getPriorityValue() {
    switch (priority.toLowerCase()) {
      case "high":
        return 3;
      case "medium":
        return 2;
      case "low":
        return 1;
      default:
        return 0;
    }
  }
}
