# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Identity @Identity @ErrorHandling @BTL
Feature: Identity Exception Handling
  As a framework developer
  I want proper exception handling for identity operations
  So that the system fails safely and provides clear error messages

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Attempt to create identity with null arguments
    When I attempt to create an identity with null arguments
    Then identity creation should fail with exception containing "required arguments cannot be null"
    And the exception should be of type "IllegalArgumentException"

  Scenario: Attempt to parse invalid identity string
    When I attempt to parse an identity from invalid string "not-a-valid-identity"
    Then parsing should fail with exception containing "invalid identity format"
    And the exception should be of type "IdentityParseException"

  Scenario: Attempt to parse identity with invalid UUID
    When I attempt to parse an identity with invalid UUID "adam/not-a-valid-uuid"
    Then parsing should fail with exception containing "invalid UUID format"
    And the exception should be of type "IdentityParseException"

  Scenario: Attempt to create child identity with null parent
    When I attempt to create a child identity with null parent
    Then identity creation should fail with exception containing "parent identity cannot be null"
    And the exception should be of type "IllegalArgumentException"

  Scenario: Attempt to get parent from Adam identity
    Given an Adam component exists with reason "Parent Access Test"
    When I attempt to get the parent of the Adam identity
    Then the operation should fail with exception containing "Adam identity has no parent"
    And the exception should be of type "IdentityOperationException"

  Scenario: Attempt to lookup component with non-existent identity
    When I attempt to resolve a component with non-existent identity
    Then the lookup should return null or throw a ComponentNotFoundException

  Scenario: Exceed maximum hierarchy depth
    Given a component hierarchy at the maximum allowed depth
    When I attempt to create a child exceeding the maximum depth
    Then identity creation should fail with exception containing "maximum hierarchy depth exceeded"
    And the exception should be of type "IdentityHierarchyException"

  @Resilience
  Scenario: Handle concurrent identity creation conflicts
    When I simulate concurrent creation of components with the same parent
    Then no duplicate identities should be created
    And all created identities should be unique
    And no exceptions should be leaked to the caller
    And the system should resolve conflicts automatically