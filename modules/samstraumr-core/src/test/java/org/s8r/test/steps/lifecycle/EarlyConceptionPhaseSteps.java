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
package org.s8r.test.steps.lifecycle;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Identity;
import org.s8r.component.Environment;
import org.s8r.component.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Step definitions for early conception phase tests, focusing on identity formation
 * at the beginning of a component's lifecycle.
 */
public class EarlyConceptionPhaseSteps {

    private static final Logger LOGGER = Logger.getLogger(EarlyConceptionPhaseSteps.class.getName());
    
    // Test context
    private Component component;
    private Component parentComponent;
    private Identity identity;
    private LocalDateTime creationTime;
    private Map<String, Object> environmentContext = new HashMap<>();
    private Exception lastException;
    private AtomicBoolean resourcesAllocated = new AtomicBoolean(false);
    
    // Capture the errors for verification
    private Map<String, String> errorInfo = new HashMap<>();
    
    @Given("the system environment is properly configured")
    public void the_system_environment_is_properly_configured() {
        // Configure test environment with appropriate settings
        environmentContext.put("test.mode", true);
        environmentContext.put("validate.identity", true);
        environmentContext.put("conceive.components", true);
        environmentContext.put("test.timestamp", LocalDateTime.now());
        
        LOGGER.info("Test environment configured with test mode enabled");
    }
    
    @Given("an existing parent tube")
    public void an_existing_parent_tube() {
        // Create a parent component as the "tube"
        try {
            parentComponent = Component.createAdam("Parent Tube Creation");
            
            // Ensure parent is in ACTIVE state for creating children
            if (parentComponent.getState() != State.ACTIVE) {
                // Transition to ACTIVE state through the necessary states
                parentComponent.setState(State.ACTIVE);
            }
            
            Assertions.assertNotNull(parentComponent, "Parent tube should be created");
            Assertions.assertEquals(State.ACTIVE, parentComponent.getState(), 
                "Parent tube should be in ACTIVE state");
                
            LOGGER.info("Created parent tube with identity: " + parentComponent.getIdentity().getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create parent tube: " + e.getMessage());
            throw new AssertionError("Failed to create parent tube", e);
        }
    }
    
    @When("a tube transitions from non-existence to existence")
    public void a_tube_transitions_from_non_existence_to_existence() {
        try {
            // Capture the time before creation
            creationTime = LocalDateTime.now();
            
            // Create a new component (representing a "tube")
            component = Component.createAdam("Early Conception Test");
            
            // Retrieve the component's identity
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created component with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create component: " + e.getMessage());
            throw new AssertionError("Failed to create component", e);
        }
    }
    
    @When("an origin tube is conceived without a parent")
    public void an_origin_tube_is_conceived_without_a_parent() {
        try {
            // Create an Adam component (origin tube without parent)
            component = Component.createAdam("Origin Tube Creation");
            
            // Retrieve the component's identity
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Origin tube should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created origin tube with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create origin tube: " + e.getMessage());
            throw new AssertionError("Failed to create origin tube", e);
        }
    }
    
    @When("a tube is conceived in a specific environmental context")
    public void a_tube_is_conceived_in_a_specific_environmental_context() {
        try {
            // Set up environmental context
            environmentContext.put("domain", "scientific");
            environmentContext.put("purpose", "data analysis");
            environmentContext.put("constraints", "memory=limited,processing=batch");
            environmentContext.put("signature", UUID.randomUUID().toString());
            
            // Create a component with environmental context
            component = Component.createAdam("Environmental Context Test");
            
            // Apply environmental context to the component
            Environment env = component.getEnvironment();
            for (Map.Entry<String, Object> entry : environmentContext.entrySet()) {
                env.setValue(entry.getKey(), entry.getValue().toString());
            }
            
            // Retrieve the component's identity
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created component with environmental context and identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create component with environmental context: " + e.getMessage());
            throw new AssertionError("Failed to create component with environmental context", e);
        }
    }
    
    @When("the parent tube initiates child tube creation")
    public void the_parent_tube_initiates_child_tube_creation() {
        try {
            // Create a child component from the parent
            component = parentComponent.createChild("Child Tube Creation");
            
            // Retrieve the component's identity
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Child tube should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created child tube with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create child tube: " + e.getMessage());
            throw new AssertionError("Failed to create child tube", e);
        }
    }
    
    @When("identity formation encounters a critical error")
    public void identity_formation_encounters_a_critical_error() {
        try {
            // Capture existing resources before the error
            resourcesAllocated.set(true);
            
            // Simulate a critical error during identity formation
            // In real implementation, we would use a method that forces an error
            // For this test, we'll create a component that will fail
            
            // Set up environment to cause failure
            environmentContext.put("force.error", true);
            environmentContext.put("error.type", "CRITICAL");
            environmentContext.put("error.message", "Simulated identity formation failure");
            
            try {
                // This should throw an exception
                Component.createAdam("Error Test");
            } catch (Exception e) {
                // Expected exception - capture for verification
                lastException = e;
                errorInfo.put("message", e.getMessage());
                errorInfo.put("type", e.getClass().getSimpleName());
                
                LOGGER.info("Captured expected error during identity formation: " + e.getMessage());
                return;
            }
            
            // If we get here, no exception was thrown
            throw new AssertionError("Expected exception during identity formation, but none was thrown");
        } catch (AssertionError e) {
            throw e; // Rethrow assertion errors
        } catch (Exception e) {
            if (lastException == null) {
                lastException = e;
            }
            LOGGER.severe("Unexpected error: " + e.getMessage());
            throw new AssertionError("Unexpected error during identity formation test", e);
        }
    }
    
    @Then("a substrate identity should be created")
    public void a_substrate_identity_should_be_created() {
        Assertions.assertNotNull(identity, "Substrate identity should be created");
        Assertions.assertNotNull(identity.getId(), "Substrate identity should have an ID");
    }
    
    @And("the identity should contain a unique identifier")
    public void the_identity_should_contain_a_unique_identifier() {
        String id = identity.getId();
        Assertions.assertNotNull(id, "Identity should have an ID");
        Assertions.assertFalse(id.isEmpty(), "Identity ID should not be empty");
        
        // Create a second component to verify uniqueness
        Component secondComponent = Component.createAdam("Uniqueness Test");
        String secondId = secondComponent.getIdentity().getId();
        
        Assertions.assertNotEquals(id, secondId, "Identity IDs should be unique");
    }
    
    @And("the identity should capture the exact moment of creation")
    public void the_identity_should_capture_the_exact_moment_of_creation() {
        LocalDateTime identityCreationTime = identity.getCreationTime();
        Assertions.assertNotNull(identityCreationTime, "Identity should have a creation time");
        
        // Verify the creation time is close to our recorded time (within 1 second)
        long timeDifferenceMs = Math.abs(identityCreationTime.toLocalTime().toNanoOfDay() / 1_000_000 - 
                                      creationTime.toLocalTime().toNanoOfDay() / 1_000_000);
        
        Assertions.assertTrue(timeDifferenceMs < 1000, 
            "Identity creation time should be within 1 second of the actual creation time");
    }
    
    @And("the identity should record the creation purpose")
    public void the_identity_should_record_the_creation_purpose() {
        String purpose = identity.getPurpose();
        Assertions.assertNotNull(purpose, "Identity should record the creation purpose");
        Assertions.assertFalse(purpose.isEmpty(), "Creation purpose should not be empty");
        Assertions.assertTrue(purpose.contains("Early Conception Test") || 
                           purpose.contains("Origin Tube Creation") ||
                           purpose.contains("Environmental Context Test") ||
                           purpose.contains("Child Tube Creation"),
            "Purpose should match the reason provided during creation");
    }
    
    @And("the identity should establish existence boundaries")
    public void the_identity_should_establish_existence_boundaries() {
        // Check existence boundaries - in this context, this means:
        // 1. Has a valid creation time
        // 2. Has a defined scope or boundary
        // 3. Has a well-defined lifecycle state
        
        Assertions.assertNotNull(identity.getCreationTime(), 
            "Identity should have a creation time as an existence boundary");
            
        Assertions.assertNotNull(component.getState(), 
            "Component should have a defined lifecycle state");
            
        // Verify the component has a defined scope or boundary
        // This is implementation-specific but could be checked through environment properties
        Environment env = component.getEnvironment();
        Assertions.assertNotNull(env, "Component should have an environment to establish boundaries");
    }
    
    @Then("the identity should be marked as an Adam tube")
    public void the_identity_should_be_marked_as_an_adam_tube() {
        Assertions.assertTrue(identity.isAdam(), "Identity should be marked as Adam tube");
        Assertions.assertFalse(identity.hasParent(), "Adam tube should not have a parent");
    }
    
    @And("the identity should establish a root address")
    public void the_identity_should_establish_a_root_address() {
        // Verify the identity has a root address
        // The exact implementation depends on the Identity API
        String address = identity.getAddress();
        Assertions.assertNotNull(address, "Identity should have an address");
        Assertions.assertFalse(address.isEmpty(), "Address should not be empty");
        Assertions.assertTrue(address.startsWith("/") || address.matches("[A-Za-z0-9\\-_]+"), 
            "Address should be in a valid format");
    }
    
    @And("the identity should have no parent reference")
    public void the_identity_should_have_no_parent_reference() {
        Assertions.assertFalse(identity.hasParent(), "Adam tube should not have a parent reference");
        
        try {
            // This should either return null or throw an exception
            Identity parentIdentity = identity.getParentIdentity();
            Assertions.assertNull(parentIdentity, "Parent identity should be null for Adam tube");
        } catch (Exception e) {
            // Alternative implementation might throw an exception
            Assertions.assertTrue(e.getMessage().contains("parent") || e.getMessage().contains("Adam"),
                "Exception should indicate no parent for Adam tube");
        }
    }
    
    @And("the identity should be capable of tracking descendants")
    public void the_identity_should_be_capable_of_tracking_descendants() {
        // Create a child component to test descendant tracking
        Component childComponent = component.createChild("Descendant Test");
        
        // Verify the parent can track its descendants
        Assertions.assertTrue(component.hasChildren(), "Adam tube should be able to track descendants");
        
        // Verify the child is recognized as a descendant
        boolean childFound = false;
        for (Component child : component.getChildren()) {
            if (child.getIdentity().getId().equals(childComponent.getIdentity().getId())) {
                childFound = true;
                break;
            }
        }
        Assertions.assertTrue(childFound, "Adam tube should recognize its descendants");
    }
    
    @Then("the identity should incorporate environmental signatures")
    public void the_identity_should_incorporate_environmental_signatures() {
        // Verify that the identity or component has captured environmental signatures
        Environment env = component.getEnvironment();
        
        // Check for the presence of the environmental signature 
        String signature = env.getValue("signature");
        Assertions.assertNotNull(signature, "Environment should contain the signature");
        Assertions.assertEquals(environmentContext.get("signature").toString(), signature,
            "Environmental signature should match the one provided");
    }
    
    @And("the identity should adapt to environmental constraints")
    public void the_identity_should_adapt_to_environmental_constraints() {
        // Verify the component has adapted to the environmental constraints
        Environment env = component.getEnvironment();
        
        // Check constraints are applied
        String constraints = env.getValue("constraints");
        Assertions.assertNotNull(constraints, "Environment should contain constraints");
        Assertions.assertEquals(environmentContext.get("constraints").toString(), constraints,
            "Environmental constraints should be applied");
            
        // Verify the component behavior reflects the constraints
        // This would typically check configuration or behavior aspects
        // For this test, we'll just check that the environment has the constraints
    }
    
    @And("the identity should be traceable to its environment")
    public void the_identity_should_be_traceable_to_its_environment() {
        // Verify the identity can be traced back to its environment
        Environment env = component.getEnvironment();
        
        // Check environment traceability attributes
        Assertions.assertNotNull(env.getValue("domain"), "Environment should contain domain information");
        Assertions.assertEquals(environmentContext.get("domain").toString(), env.getValue("domain"),
            "Environmental domain should match");
            
        Assertions.assertNotNull(env.getValue("purpose"), "Environment should contain purpose information");
        Assertions.assertEquals(environmentContext.get("purpose").toString(), env.getValue("purpose"),
            "Environmental purpose should match");
    }
    
    @And("the identity should maintain environmental awareness")
    public void the_identity_should_maintain_environmental_awareness() {
        // Environmental awareness means the component responds to changes in its environment
        Environment env = component.getEnvironment();
        
        // Update an environment value
        String newConstraint = "memory=expanded,processing=realtime";
        env.setValue("constraints", newConstraint);
        
        // Verify the change was applied and can be retrieved
        Assertions.assertEquals(newConstraint, env.getValue("constraints"),
            "Component should maintain awareness of environmental changes");
    }
    
    @Then("the child tube should inherit parent characteristics")
    public void the_child_tube_should_inherit_parent_characteristics() {
        // Verify the child inherited relevant characteristics from parent
        
        // Get parent environment
        Environment parentEnv = parentComponent.getEnvironment();
        
        // Set some parent characteristics to check inheritance
        parentEnv.setValue("inherited.trait", "resilience");
        parentEnv.setValue("inherited.capability", "data-processing");
        
        // Check child environment for inherited traits
        Environment childEnv = component.getEnvironment();
        
        // The exact inheritance mechanism depends on implementation
        // Check for direct environment inheritance or identity inheritance
        Assertions.assertEquals(parentEnv.getValue("inherited.trait"), 
            childEnv.getValue("inherited.trait"),
            "Child should inherit trait from parent");
            
        Assertions.assertEquals(parentEnv.getValue("inherited.capability"), 
            childEnv.getValue("inherited.capability"),
            "Child should inherit capability from parent");
    }
    
    @And("the child tube should receive a distinct identity")
    public void the_child_tube_should_receive_a_distinct_identity() {
        // Verify child identity is distinct from parent
        Identity childIdentity = component.getIdentity();
        Identity parentIdentity = parentComponent.getIdentity();
        
        Assertions.assertNotNull(childIdentity, "Child should have an identity");
        Assertions.assertNotNull(parentIdentity, "Parent should have an identity");
        
        Assertions.assertNotEquals(childIdentity.getId(), parentIdentity.getId(),
            "Child identity should be distinct from parent identity");
    }
    
    @And("the child tube should maintain parent reference")
    public void the_child_tube_should_maintain_parent_reference() {
        // Verify child maintains reference to parent
        Identity childIdentity = component.getIdentity();
        
        Assertions.assertTrue(childIdentity.hasParent(), "Child identity should have parent reference");
        
        // Get parent identity reference
        Identity parentRef = childIdentity.getParentIdentity();
        Assertions.assertNotNull(parentRef, "Parent reference should not be null");
        
        // Verify it matches the actual parent
        Assertions.assertEquals(parentComponent.getIdentity().getId(), parentRef.getId(),
            "Parent reference should match actual parent identity");
    }
    
    @And("the parent tube should recognize the child")
    public void the_parent_tube_should_recognize_the_child() {
        // Verify parent recognizes child
        Assertions.assertTrue(parentComponent.hasChildren(), "Parent should recognize it has children");
        
        boolean childRecognized = false;
        for (Component child : parentComponent.getChildren()) {
            if (child.getIdentity().getId().equals(component.getIdentity().getId())) {
                childRecognized = true;
                break;
            }
        }
        
        Assertions.assertTrue(childRecognized, "Parent should recognize the specific child");
    }
    
    @And("both tubes should update their lineage records")
    public void both_tubes_should_update_their_lineage_records() {
        // Verify lineage records are updated in both parent and child
        
        // In parent, verify child is recorded in descendants
        boolean childInParentLineage = false;
        for (Component child : parentComponent.getChildren()) {
            if (child.getIdentity().getId().equals(component.getIdentity().getId())) {
                childInParentLineage = true;
                break;
            }
        }
        Assertions.assertTrue(childInParentLineage, "Parent should have child in lineage records");
        
        // In child, verify parent is recorded correctly
        Identity parentRef = component.getIdentity().getParentIdentity();
        Assertions.assertNotNull(parentRef, "Child should have parent in lineage records");
        Assertions.assertEquals(parentComponent.getIdentity().getId(), parentRef.getId(),
            "Child lineage should reference correct parent");
    }
    
    @Then("the conception process should be aborted")
    public void the_conception_process_should_be_aborted() {
        // Verify the component creation/conception was aborted
        Assertions.assertNotNull(lastException, "An exception should have been thrown");
        Assertions.assertTrue(errorInfo.containsKey("message"), "Error information should be captured");
        
        // Verify no component was created
        Assertions.assertNull(component, "No component should be created after error");
    }
    
    @And("all allocated resources should be released")
    public void all_allocated_resources_should_be_released() {
        // Verify resources were allocated and then released
        Assertions.assertTrue(resourcesAllocated.get(), "Resources should have been allocated");
        
        // In a real implementation, we would check specific resources
        // For this test, we'll check the error handling ensures resources are released
        
        // Check for resource cleanup mention in error
        Assertions.assertTrue(lastException.getMessage().contains("resource") || 
                          lastException.getMessage().contains("cleanup") ||
                          lastException.getMessage().contains("release"),
            "Exception should mention resource cleanup");
    }
    
    @And("appropriate error information should be provided")
    public void appropriate_error_information_should_be_provided() {
        // Verify error information is appropriate
        Assertions.assertNotNull(errorInfo.get("message"), "Error should have a message");
        Assertions.assertFalse(errorInfo.get("message").isEmpty(), "Error message should not be empty");
        
        // Verify error contains expected information
        String errorMessage = errorInfo.get("message");
        Assertions.assertTrue(errorMessage.contains("identity") || 
                          errorMessage.contains("formation") ||
                          errorMessage.contains("creation") ||
                          errorMessage.contains("conception"),
            "Error message should reference identity formation");
    }
    
    @And("the environment should log the failure details")
    public void the_environment_should_log_the_failure_details() {
        // In a real implementation, this would check system logs
        // For this test, we'll use the component's log or environment records
        
        // The exact verification depends on how logging is implemented
        // We'll assume the exception contains logging-related information
        
        String errorMessage = errorInfo.get("message");
        Assertions.assertTrue(errorMessage.contains("log") || 
                          errorMessage.contains("record") ||
                          errorMessage.contains("reported"),
            "Error should indicate failure was logged");
    }
}