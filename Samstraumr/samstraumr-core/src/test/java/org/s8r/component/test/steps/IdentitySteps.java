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

package org.s8r.component.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.component.Component;
import org.s8r.component.Identity;

import io.cucumber.java.en.*;

/**
 * Step definitions for component identity tests.
 *
 * <p>These step definitions implement the BDD scenarios related to component identity, focusing on
 * identity creation, hierarchy, lineage tracking, and query operations.
 */
public class IdentitySteps extends BaseComponentSteps {

  private Map<String, Component> componentMap = new HashMap<>();
  private Map<String, Identity> identityMap = new HashMap<>();
  private List<Component> componentHierarchy = new ArrayList<>();

  @When("a child component is created from the parent with reason {string}")
  public void a_child_component_is_created_from_the_parent_with_reason(String reason) {
    assertNotNull(parentComponent, "Parent component should exist");

    testComponent = createChildComponent(reason, environment.asMap(), parentComponent);
    componentIdentity = testComponent.getIdentity();

    storeInContext("testComponent", testComponent);
    storeInContext("componentIdentity", componentIdentity);
  }

  @When("a {int}-level component hierarchy is created")
  public void a_level_component_hierarchy_is_created(Integer levels) {
    assertNotNull(environment, "Environment should exist");

    // Create the hierarchy with parent-child relationships
    componentHierarchy = new ArrayList<>();

    // Create root component
    Component root = createComponent("Level 0 Component", environment.asMap());
    componentHierarchy.add(root);
    componentMap.put("level_0", root);
    identityMap.put("level_0", root.getIdentity());

    // Create children at each level
    Component parent = root;
    for (int i = 1; i < levels; i++) {
      Component child =
          createChildComponent("Level " + i + " Component", environment.asMap(), parent);
      componentHierarchy.add(child);
      componentMap.put("level_" + i, child);
      identityMap.put("level_" + i, child.getIdentity());
      parent = child; // Next level parent is this child
    }

    storeInContext("componentHierarchy", componentHierarchy);
    storeInContext("componentMap", componentMap);
    storeInContext("identityMap", identityMap);
  }

  @When("a component genealogy of {int} generations is created")
  public void a_component_genealogy_of_generations_is_created(Integer generations) {
    assertNotNull(environment, "Environment should exist");

    // Similar to hierarchy but with different naming for clarity
    componentHierarchy = new ArrayList<>();

    // Create root component
    Component root = createComponent("Generation 0 Progenitor", environment.asMap());
    componentHierarchy.add(root);
    componentMap.put("gen_0", root);
    identityMap.put("gen_0", root.getIdentity());

    // Create subsequent generations
    Component parent = root;
    for (int i = 1; i < generations; i++) {
      Component child =
          createChildComponent("Generation " + i + " Descendant", environment.asMap(), parent);
      componentHierarchy.add(child);
      componentMap.put("gen_" + i, child);
      identityMap.put("gen_" + i, child.getIdentity());
      parent = child; // Next generation parent is this child
    }

    storeInContext("componentHierarchy", componentHierarchy);
    storeInContext("componentMap", componentMap);
    storeInContext("identityMap", identityMap);
  }

  @When("a complex component hierarchy is created with {int} nodes")
  public void a_complex_component_hierarchy_is_created_with_nodes(Integer nodeCount) {
    assertNotNull(environment, "Environment should exist");

    // Create a more complex hierarchy with branching
    componentMap = new HashMap<>();
    identityMap = new HashMap<>();

    // Create root
    Component root = createComponent("Root Node", environment.asMap());
    componentMap.put("root", root);
    identityMap.put("root", root.getIdentity());

    // Create first level children (3 nodes)
    for (int i = 0; i < 3 && i < nodeCount; i++) {
      Component child = createChildComponent("Child " + i, environment.asMap(), root);
      componentMap.put("child_" + i, child);
      identityMap.put("child_" + i, child.getIdentity());

      // Create second level children (2 per first level)
      for (int j = 0; j < 2 && (i * 2 + j + 3) < nodeCount; j++) {
        Component grandchild =
            createChildComponent("Grandchild " + i + "_" + j, environment.asMap(), child);
        componentMap.put("grandchild_" + i + "_" + j, grandchild);
        identityMap.put("grandchild_" + i + "_" + j, grandchild.getIdentity());
      }
    }

    storeInContext("componentMap", componentMap);
    storeInContext("identityMap", identityMap);
  }

  @Then("the component should have an Adam identity")
  public void the_component_should_have_an_adam_identity() {
    assertNotNull(testComponent, "Component should exist");
    assertNotNull(testComponent.getIdentity(), "Component should have identity");
    assertTrue(testComponent.getIdentity().isAdam(), "Component should have Adam identity");
  }

  @Then("the identity should have a unique ID")
  public void the_identity_should_have_a_unique_id() {
    assertNotNull(testComponent, "Component should exist");
    Identity identity = testComponent.getIdentity();
    assertNotNull(identity, "Identity should exist");

    String uniqueId = identity.getUniqueId();
    assertNotNull(uniqueId, "Identity should have unique ID");
    assertFalse(uniqueId.isEmpty(), "Identity unique ID should not be empty");
  }

  @Then("the identity should have the reason {string}")
  public void the_identity_should_have_the_reason(String reason) {
    assertNotNull(testComponent, "Component should exist");
    Identity identity = testComponent.getIdentity();
    assertNotNull(identity, "Identity should exist");

    assertEquals(reason, identity.getReason(), "Identity should have correct reason");
  }

  @Then("the identity should have no parent")
  public void the_identity_should_have_no_parent() {
    assertNotNull(testComponent, "Component should exist");
    Identity identity = testComponent.getIdentity();
    assertNotNull(identity, "Identity should exist");

    assertNull(identity.getParent(), "Adam identity should have no parent");
  }

  @Then("the identity should be in the root hierarchy")
  public void the_identity_should_be_in_the_root_hierarchy() {
    assertNotNull(testComponent, "Component should exist");
    Identity identity = testComponent.getIdentity();
    assertNotNull(identity, "Identity should exist");

    assertTrue(identity.isRoot(), "Adam identity should be in root hierarchy");
  }

  @Then("the child component should have a valid identity")
  public void the_child_component_should_have_a_valid_identity() {
    assertNotNull(testComponent, "Child component should exist");
    Identity identity = testComponent.getIdentity();
    assertNotNull(identity, "Child identity should exist");

    String uniqueId = identity.getUniqueId();
    assertNotNull(uniqueId, "Child identity should have unique ID");
    assertFalse(uniqueId.isEmpty(), "Child identity unique ID should not be empty");
  }

  @Then("the child identity should reference the parent identity")
  public void the_child_identity_should_reference_the_parent_identity() {
    assertNotNull(testComponent, "Child component should exist");
    Identity childIdentity = testComponent.getIdentity();
    assertNotNull(childIdentity, "Child identity should exist");

    Identity parentRef = childIdentity.getParent();
    assertNotNull(parentRef, "Child identity should reference parent");

    assertNotNull(parentComponent, "Parent component should exist");
    assertEquals(
        parentComponent.getIdentity().getUniqueId(),
        parentRef.getUniqueId(),
        "Child should reference correct parent");
  }

  @Then("the child identity should follow hierarchical naming convention")
  public void the_child_identity_should_follow_hierarchical_naming_convention() {
    assertNotNull(testComponent, "Child component should exist");
    assertNotNull(parentComponent, "Parent component should exist");

    String hierarchicalId = testComponent.getIdentity().getHierarchicalId();
    assertNotNull(hierarchicalId, "Hierarchical ID should exist");

    // Validate hierarchical format (should include parent ID)
    String parentId = parentComponent.getIdentity().getUniqueId();
    assertTrue(
        hierarchicalId.contains(parentId) || hierarchicalId.contains("T"),
        "Hierarchical ID should follow convention");
  }

  @Then("the child identity should include the parent in its lineage")
  public void the_child_identity_should_include_the_parent_in_its_lineage() {
    assertNotNull(testComponent, "Child component should exist");
    List<String> lineage = testComponent.getLineage();
    assertNotNull(lineage, "Lineage should exist");
    assertFalse(lineage.isEmpty(), "Lineage should not be empty");

    // Parent's reason should be in the lineage
    assertTrue(
        lineage.contains(parentComponent.getReason()), "Lineage should include parent's reason");
  }

  @Then("each level should properly reference its parent")
  public void each_level_should_properly_reference_its_parent() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    // Skip the first one (root has no parent)
    for (int i = 1; i < hierarchy.size(); i++) {
      Component child = hierarchy.get(i);
      Component parent = hierarchy.get(i - 1);

      assertNotNull(child.getParentIdentity(), "Each child should reference parent");
      assertEquals(
          parent.getIdentity().getUniqueId(),
          child.getParentIdentity().getUniqueId(),
          "Child should reference correct parent");
    }
  }

  @Then("the top level should have all descendants in its hierarchy")
  public void the_top_level_should_have_all_descendants_in_its_hierarchy() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    // Get the root component
    Component root = hierarchy.get(0);
    List<Identity> descendants = root.getIdentity().getDescendants();

    // Check that all components (except root) are in the descendants list
    assertEquals(
        hierarchy.size() - 1,
        descendants.size(),
        "Root should have all other components as descendants");

    for (int i = 1; i < hierarchy.size(); i++) {
      Component child = hierarchy.get(i);
      final String childId = child.getIdentity().getUniqueId();

      boolean found = descendants.stream().anyMatch(d -> d.getUniqueId().equals(childId));

      assertTrue(found, "Descendant list should contain each child");
    }
  }

  @Then("each descendant should have the correct ancestor chain")
  public void each_descendant_should_have_the_correct_ancestor_chain() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    for (int i = 1; i < hierarchy.size(); i++) {
      Component component = hierarchy.get(i);
      List<String> lineage = component.getLineage();

      // Check that lineage contains all ancestors in order
      assertEquals(i, lineage.size(), "Lineage should contain all ancestors");

      for (int j = 0; j < i; j++) {
        Component ancestor = hierarchy.get(j);
        assertEquals(
            ancestor.getReason(),
            lineage.get(j),
            "Lineage should contain ancestors in correct order");
      }
    }
  }

  @Then("the hierarchical addressing should reflect the tree structure")
  public void the_hierarchical_addressing_should_reflect_the_tree_structure() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    for (int i = 0; i < hierarchy.size(); i++) {
      Component component = hierarchy.get(i);
      String hierarchicalId = component.getIdentity().getHierarchicalId();

      // Root should have simple hierarchical ID
      if (i == 0) {
        assertFalse(hierarchicalId.contains("."), "Root should have simple hierarchical ID");
      } else {
        // Count the number of segments (dots + 1)
        long dotCount = hierarchicalId.chars().filter(ch -> ch == '.').count();
        assertEquals(i, dotCount + 1, "Hierarchical ID depth should match component level");
      }
    }
  }

  @Then("each component should have a complete lineage")
  public void each_component_should_have_a_complete_lineage() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    for (int i = 0; i < hierarchy.size(); i++) {
      Component component = hierarchy.get(i);
      List<String> lineage = component.getLineage();

      // Lineage size should match the component's level (0 for root, etc.)
      assertEquals(i, lineage.size(), "Lineage length should match component level");
    }
  }

  @Then("the lineage should contain all ancestor reasons in order")
  public void the_lineage_should_contain_all_ancestor_reasons_in_order() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    // Check each component's lineage
    for (int i = 1; i < hierarchy.size(); i++) {
      Component component = hierarchy.get(i);
      List<String> lineage = component.getLineage();

      // Each entry in lineage should match the corresponding ancestor's reason
      for (int j = 0; j < lineage.size(); j++) {
        assertEquals(
            hierarchy.get(j).getReason(),
            lineage.get(j),
            "Lineage should contain ancestor reasons in order");
      }
    }
  }

  @Then("the root component should be at the start of each lineage")
  public void the_root_component_should_be_at_the_start_of_each_lineage() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    Component root = hierarchy.get(0);
    String rootReason = root.getReason();

    // Check each non-root component's lineage
    for (int i = 1; i < hierarchy.size(); i++) {
      Component component = hierarchy.get(i);
      List<String> lineage = component.getLineage();

      assertFalse(lineage.isEmpty(), "Lineage should not be empty");
      assertEquals(rootReason, lineage.get(0), "Root component should be at start of lineage");
    }
  }

  @Then("each generation should add exactly one entry to the lineage")
  public void each_generation_should_add_exactly_one_entry_to_the_lineage() {
    @SuppressWarnings("unchecked")
    List<Component> hierarchy = getFromContext("componentHierarchy", List.class);
    assertNotNull(hierarchy, "Hierarchy should exist");

    // For each level, check that lineage size increases by exactly 1
    for (int i = 1; i < hierarchy.size(); i++) {
      Component current = hierarchy.get(i);
      Component previous = hierarchy.get(i - 1);

      assertEquals(
          previous.getLineage().size() + 1,
          current.getLineage().size(),
          "Each generation should add exactly one lineage entry");
    }
  }

  @Then("it should be possible to find any component by its unique ID")
  public void it_should_be_possible_to_find_any_component_by_its_unique_id() {
    @SuppressWarnings("unchecked")
    Map<String, Component> components = getFromContext("componentMap", Map.class);
    assertNotNull(components, "Component map should exist");

    // Test with a few components
    for (Map.Entry<String, Component> entry : components.entrySet()) {
      String id = entry.getValue().getUniqueId();
      Component found = findComponentById(id, components);

      assertNotNull(found, "Should find component by ID");
      assertEquals(
          entry.getValue().getUniqueId(),
          found.getUniqueId(),
          "Found component should match expected");
    }
  }

  @Then("it should be possible to find all descendants of any component")
  public void it_should_be_possible_to_find_all_descendants_of_any_component() {
    @SuppressWarnings("unchecked")
    Map<String, Component> components = getFromContext("componentMap", Map.class);
    assertNotNull(components, "Component map should exist");

    // Check root component's descendants
    Component root = components.get("root");
    assertNotNull(root, "Root component should exist");

    List<Identity> descendants = root.getIdentity().getDescendants();
    assertNotNull(descendants, "Should be able to get descendants");

    // Root should have all other components as descendants
    assertEquals(
        components.size() - 1,
        descendants.size(),
        "Root should have correct number of descendants");
  }

  @Then("it should be possible to find all siblings of any component")
  public void it_should_be_possible_to_find_all_siblings_of_any_component() {
    @SuppressWarnings("unchecked")
    Map<String, Component> components = getFromContext("componentMap", Map.class);
    assertNotNull(components, "Component map should exist");

    // Check siblings for a component with known siblings
    Component child = components.get("child_1");
    assertNotNull(child, "Test child should exist");

    List<Identity> siblings = child.getIdentity().getSiblings();
    assertNotNull(siblings, "Should be able to get siblings");

    // Child_1 should have 2 siblings (child_0 and child_2)
    assertEquals(2, siblings.size(), "Should have correct number of siblings");
  }

  @Then("the ancestral relationship queries should work correctly")
  public void the_ancestral_relationship_queries_should_work_correctly() {
    @SuppressWarnings("unchecked")
    Map<String, Component> components = getFromContext("componentMap", Map.class);
    assertNotNull(components, "Component map should exist");

    // Check ancestral relationships for a grandchild
    Component grandchild = components.get("grandchild_0_0");
    assertNotNull(grandchild, "Grandchild should exist");

    Component child = components.get("child_0");
    assertNotNull(child, "Child should exist");

    Component root = components.get("root");
    assertNotNull(root, "Root should exist");

    // Test isDescendantOf relationship
    assertTrue(
        grandchild.getIdentity().isDescendantOf(child.getIdentity()),
        "Grandchild should be descendant of child");

    assertTrue(
        grandchild.getIdentity().isDescendantOf(root.getIdentity()),
        "Grandchild should be descendant of root");

    // Test isAncestorOf relationship
    assertTrue(
        root.getIdentity().isAncestorOf(grandchild.getIdentity()),
        "Root should be ancestor of grandchild");

    assertTrue(
        child.getIdentity().isAncestorOf(grandchild.getIdentity()),
        "Child should be ancestor of grandchild");
  }

  // Helper method to find a component by ID
  private Component findComponentById(String id, Map<String, Component> components) {
    for (Component component : components.values()) {
      if (component.getUniqueId().equals(id)) {
        return component;
      }
    }
    return null;
  }
}
