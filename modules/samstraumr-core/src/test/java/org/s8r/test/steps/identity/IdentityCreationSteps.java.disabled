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
package org.s8r.test.steps.identity;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Identity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Step definitions for identity creation feature tests.
 */
public class IdentityCreationSteps {

    // Test context to share between steps
    private Component adamComponent;
    private Component childComponent;
    private Component grandchildComponent;
    private List<Component> components = new ArrayList<>();
    private Exception lastException;
    private Identity serializedIdentity;
    private Identity deserializedIdentity;
    private long startTime;
    private long endTime;

    @When("I create an Adam component with reason {string}")
    public void i_create_an_adam_component_with_reason(String reason) {
        try {
            adamComponent = Component.createAdam(reason);
            components.add(adamComponent);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the component should have a valid identity")
    public void the_component_should_have_a_valid_identity() {
        Component component = adamComponent != null ? adamComponent : childComponent;
        Assertions.assertNotNull(component, "Component should be created");
        Assertions.assertNotNull(component.getIdentity(), "Component should have identity");
    }

    @Then("the identity should be an Adam identity")
    public void the_identity_should_be_an_adam_identity() {
        Assertions.assertNotNull(adamComponent, "Adam component should be created");
        Identity identity = adamComponent.getIdentity();
        
        Assertions.assertTrue(identity.isAdam(), "Identity should be an Adam identity");
        Assertions.assertFalse(identity.isChild(), "Adam identity should not be a child identity");
    }

    @Then("the identity should have a UUID format")
    public void the_identity_should_have_a_uuid_format() {
        Component component = adamComponent != null ? adamComponent : childComponent;
        Identity identity = component.getIdentity();
        
        String identityStr = identity.toString();
        
        // Extract UUID from identity string pattern
        String uuidPattern;
        if (identity.isAdam()) {
            uuidPattern = identityStr.substring(identityStr.indexOf("/") + 1);
        } else {
            uuidPattern = identityStr.substring(identityStr.lastIndexOf("/") + 1);
        }
        
        try {
            UUID uuid = UUID.fromString(uuidPattern);
            Assertions.assertNotNull(uuid, "Should be valid UUID");
        } catch (IllegalArgumentException e) {
            Assertions.fail("Identity does not contain a valid UUID: " + e.getMessage());
        }
    }

    @Then("the identity should have no parent reference")
    public void the_identity_should_have_no_parent_reference() {
        Assertions.assertNotNull(adamComponent, "Adam component should be created");
        Identity identity = adamComponent.getIdentity();
        
        Assertions.assertThrows(Exception.class, () -> identity.getParentIdentity(),
            "Adam identity should not have a parent reference");
    }

    @Then("the identity should include a creation timestamp")
    public void the_identity_should_include_a_creation_timestamp() {
        Component component = adamComponent != null ? adamComponent : childComponent;
        Identity identity = component.getIdentity();
        
        LocalDateTime creationTime = identity.getCreationTime();
        Assertions.assertNotNull(creationTime, "Identity should have creation timestamp");
        
        // Check that the timestamp is reasonable (not in the future, not too far in the past)
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertTrue(creationTime.isBefore(now) || creationTime.isEqual(now),
            "Creation timestamp should not be in the future");
        
        LocalDateTime oneHourAgo = now.minusHours(1);
        Assertions.assertTrue(creationTime.isAfter(oneHourAgo),
            "Creation timestamp should be recent (within the last hour)");
    }

    @Then("the identity should match the pattern {string}")
    public void the_identity_should_match_the_pattern(String pattern) {
        Component component = adamComponent != null ? adamComponent : childComponent;
        Identity identity = component.getIdentity();
        
        String identityStr = identity.toString();
        
        // Replace placeholders in the pattern with regex patterns
        String regexPattern = pattern
            .replace("{uuid}", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
            .replace("{parent-identity}", ".*")
            .replace("{grandparent-identity}", ".*")
            .replace("{parent-uuid}", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        
        Assertions.assertTrue(identityStr.matches(regexPattern),
            "Identity " + identityStr + " should match pattern " + pattern);
    }

    @When("I create a child component with parent and reason {string}")
    public void i_create_a_child_component_with_parent_and_reason(String reason) {
        try {
            Assertions.assertNotNull(adamComponent, "Parent component must exist");
            childComponent = Component.createChild(adamComponent, reason);
            components.add(childComponent);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the identity should be a child identity")
    public void the_identity_should_be_a_child_identity() {
        Assertions.assertNotNull(childComponent, "Child component should be created");
        Identity identity = childComponent.getIdentity();
        
        Assertions.assertTrue(identity.isChild(), "Identity should be a child identity");
        Assertions.assertFalse(identity.isAdam(), "Child identity should not be an Adam identity");
    }

    @Then("the identity should include the parent's identity as prefix")
    public void the_identity_should_include_the_parents_identity_as_prefix() {
        Assertions.assertNotNull(adamComponent, "Parent component should be created");
        Assertions.assertNotNull(childComponent, "Child component should be created");
        
        String parentIdentityStr = adamComponent.getIdentity().toString();
        String childIdentityStr = childComponent.getIdentity().toString();
        
        Assertions.assertTrue(childIdentityStr.startsWith(parentIdentityStr),
            "Child identity should start with parent identity");
    }

    @Given("a child component exists with reason {string}")
    public void a_child_component_exists_with_reason(String reason) {
        Assertions.assertNotNull(adamComponent, "Parent component must exist");
        try {
            childComponent = Component.createChild(adamComponent, reason);
            components.add(childComponent);
        } catch (Exception e) {
            lastException = e;
            throw e; // Rethrow to fail the test
        }
    }

    @When("I create a child of the mid-level component with reason {string}")
    public void i_create_a_child_of_the_mid_level_component_with_reason(String reason) {
        try {
            Assertions.assertNotNull(childComponent, "Mid-level component must exist");
            grandchildComponent = Component.createChild(childComponent, reason);
            components.add(grandchildComponent);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the bottom component should have a valid identity")
    public void the_bottom_component_should_have_a_valid_identity() {
        Assertions.assertNotNull(grandchildComponent, "Bottom-level component should be created");
        Assertions.assertNotNull(grandchildComponent.getIdentity(), 
            "Bottom-level component should have identity");
    }

    @Then("the identity should be a grandchild identity")
    public void the_identity_should_be_a_grandchild_identity() {
        Assertions.assertNotNull(grandchildComponent, "Grandchild component should be created");
        Identity identity = grandchildComponent.getIdentity();
        
        Assertions.assertTrue(identity.isChild(), "Identity should be a child identity");
        
        // Check that the path has grandparent/child/parent/child/grandchild format
        String identityStr = identity.toString();
        int childCount = countOccurrences(identityStr, "/child/");
        Assertions.assertEquals(2, childCount, 
            "Grandchild identity should have 2 '/child/' sections");
    }

    @Then("the identity should contain both parent and grandparent references")
    public void the_identity_should_contain_both_parent_and_grandparent_references() {
        Assertions.assertNotNull(adamComponent, "Grandparent component should be created");
        Assertions.assertNotNull(childComponent, "Parent component should be created");
        Assertions.assertNotNull(grandchildComponent, "Grandchild component should be created");
        
        String grandparentIdentityStr = adamComponent.getIdentity().toString();
        String parentIdentityStr = childComponent.getIdentity().toString();
        String grandchildIdentityStr = grandchildComponent.getIdentity().toString();
        
        Assertions.assertTrue(grandchildIdentityStr.startsWith(grandparentIdentityStr),
            "Grandchild identity should start with grandparent identity");
        
        Assertions.assertTrue(grandchildIdentityStr.startsWith(parentIdentityStr),
            "Grandchild identity should start with parent identity");
    }

    @When("I create a component hierarchy {int} levels deep")
    public void i_create_a_component_hierarchy_n_levels_deep(int depth) {
        try {
            // Create the root component
            Component root = Component.createAdam("Root Component for Depth Test");
            components.add(root);
            
            // Create depth-1 levels of children
            Component current = root;
            for (int i = 1; i < depth; i++) {
                Component child = Component.createChild(current, "Level " + i + " Component");
                components.add(child);
                current = child;
            }
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("each component should have a valid identity")
    public void each_component_should_have_a_valid_identity() {
        for (Component component : components) {
            Assertions.assertNotNull(component.getIdentity(), 
                "Each component should have valid identity");
        }
    }

    @Then("each component's identity should contain references to all its ancestors")
    public void each_components_identity_should_contain_references_to_all_its_ancestors() {
        // For each component (except the root), check that its identity contains its parent's identity
        for (int i = 1; i < components.size(); i++) {
            Component component = components.get(i);
            Component parent = components.get(i - 1);
            
            String identityStr = component.getIdentity().toString();
            String parentIdentityStr = parent.getIdentity().toString();
            
            Assertions.assertTrue(identityStr.startsWith(parentIdentityStr),
                "Component identity should contain parent identity");
        }
    }

    @Then("the leaf component's identity should have {int} {string} sections")
    public void the_leaf_components_identity_should_have_n_sections(int count, String section) {
        // Get the last component (the leaf)
        Component leaf = components.get(components.size() - 1);
        String identityStr = leaf.getIdentity().toString();
        
        int sectionCount = countOccurrences(identityStr, section);
        Assertions.assertEquals(count, sectionCount,
            "Leaf identity should have " + count + " '" + section + "' sections");
    }

    @Then("all UUIDs in the hierarchy should be valid")
    public void all_uuids_in_the_hierarchy_should_be_valid() {
        for (Component component : components) {
            Identity identity = component.getIdentity();
            String identityStr = identity.toString();
            
            // Extract and validate UUID
            String uuidStr;
            if (identity.isAdam()) {
                uuidStr = identityStr.substring(identityStr.indexOf("/") + 1);
            } else {
                uuidStr = identityStr.substring(identityStr.lastIndexOf("/") + 1);
            }
            
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Assertions.assertNotNull(uuid, "Should be valid UUID");
            } catch (IllegalArgumentException e) {
                Assertions.fail("Identity does not contain a valid UUID: " + e.getMessage());
            }
        }
    }

    @When("I create {int} Adam components")
    public void i_create_n_adam_components(int count) {
        try {
            startTime = System.currentTimeMillis();
            
            for (int i = 0; i < count; i++) {
                Component component = Component.createAdam("Mass Creation Test " + i);
                components.add(component);
            }
            
            endTime = System.currentTimeMillis();
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("all {int} components should have unique identities")
    public void all_n_components_should_have_unique_identities(int count) {
        Assertions.assertEquals(count, components.size(), 
            "Should have created " + count + " components");
        
        Map<String, Boolean> identities = new HashMap<>();
        for (Component component : components) {
            String identityStr = component.getIdentity().toString();
            
            Assertions.assertFalse(identities.containsKey(identityStr),
                "Component identities should be unique");
                
            identities.put(identityStr, true);
        }
    }

    @Then("identity creation performance should be within acceptable limits")
    public void identity_creation_performance_should_be_within_acceptable_limits() {
        long totalTime = endTime - startTime;
        long avgTimePerComponent = totalTime / components.size();
        
        // Set a reasonable threshold (e.g., 5ms per component)
        long threshold = 5;
        
        Assertions.assertTrue(avgTimePerComponent <= threshold,
            "Average identity creation time should be <= " + threshold + 
            "ms, but was " + avgTimePerComponent + "ms");
    }

    @Then("identities should be correctly structured")
    public void identities_should_be_correctly_structured() {
        for (Component component : components) {
            Identity identity = component.getIdentity();
            String identityStr = identity.toString();
            
            if (identity.isAdam()) {
                Assertions.assertTrue(identityStr.startsWith("adam/"),
                    "Adam identity should start with 'adam/'");
            } else {
                Assertions.assertTrue(identityStr.contains("/child/"),
                    "Child identity should contain '/child/'");
            }
        }
    }

    @Then("each identity should be valid")
    public void each_identity_should_be_valid() {
        for (Component component : components) {
            Identity identity = component.getIdentity();
            
            // This assumes there's a validation method
            Assertions.assertTrue(identity.isValid(),
                "Each identity should be valid");
        }
    }

    @When("I serialize and then deserialize the component identity")
    public void i_serialize_and_then_deserialize_the_component_identity() {
        try {
            Assertions.assertNotNull(adamComponent, "Component should be created");
            serializedIdentity = adamComponent.getIdentity();
            
            // Serialize the identity
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(serializedIdentity);
            oos.close();
            
            // Deserialize the identity
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            deserializedIdentity = (Identity) ois.readObject();
            ois.close();
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the deserialized identity should match the original identity")
    public void the_deserialized_identity_should_match_the_original_identity() {
        Assertions.assertNotNull(serializedIdentity, "Original identity should exist");
        Assertions.assertNotNull(deserializedIdentity, "Deserialized identity should exist");
        
        Assertions.assertEquals(serializedIdentity.toString(), deserializedIdentity.toString(),
            "Deserialized identity should match original identity");
    }

    @Then("I should be able to resolve a component with the deserialized identity")
    public void i_should_be_able_to_resolve_a_component_with_the_deserialized_identity() {
        Assertions.assertNotNull(deserializedIdentity, "Deserialized identity should exist");
        
        // This assumes there's a resolver mechanism to find components by identity
        Component resolved = Component.resolve(deserializedIdentity);
        
        Assertions.assertNotNull(resolved, "Should be able to resolve component");
        Assertions.assertEquals(adamComponent.getIdentity(), resolved.getIdentity(),
            "Resolved component should match original component");
    }

    // Helper methods
    private int countOccurrences(String str, String subStr) {
        int count = 0;
        int lastIndex = 0;
        
        while (lastIndex != -1) {
            lastIndex = str.indexOf(subStr, lastIndex);
            
            if (lastIndex != -1) {
                count++;
                lastIndex += subStr.length();
            }
        }
        
        return count;
    }
}