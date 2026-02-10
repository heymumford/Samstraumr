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

package org.s8r.application.port.consciousness;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for component self-narrative capabilities.
 *
 * <p>This interface enables components to answer the three existential questions required for
 * Samstraumr conformance:
 *
 * <ul>
 *   <li><b>What am I?</b> - Self-description of nature and capabilities
 *   <li><b>Why do I exist?</b> - Purpose and reason for creation
 *   <li><b>Who do I relate to?</b> - Relationship network
 * </ul>
 *
 * <p>Based on the Narrative/Constructivist Model, identity is an ongoing construction - a story
 * shaped by the component itself and its social context. This port allows components to build and
 * evolve their self-narrative over time.
 *
 * <p>This supports Experiment 3: "Systems that 'explain themselves' (narrative identity) reduce
 * cognitive load for new team members."
 */
public interface NarrativePort {

  /**
   * Gets the current narrative for a component.
   *
   * @param componentId the component identifier
   * @return the component's narrative, or empty if not yet established
   */
  Optional<ComponentNarrative> getNarrative(String componentId);

  /**
   * Gets the answer to "What am I?" for a component.
   *
   * @param componentId the component identifier
   * @return the self-description, or empty if not established
   */
  Optional<String> whatAmI(String componentId);

  /**
   * Gets the answer to "Why do I exist?" for a component.
   *
   * @param componentId the component identifier
   * @return the existence rationale, or empty if not established
   */
  Optional<String> whyDoIExist(String componentId);

  /**
   * Gets the answer to "Who do I relate to?" for a component.
   *
   * @param componentId the component identifier
   * @return the relationship list, may be empty
   */
  List<String> whoDoIRelateTo(String componentId);

  /**
   * Establishes the initial narrative for a component.
   *
   * <p>This should be called during component initialization to set up the foundational
   * self-narrative.
   *
   * @param componentId the component identifier
   * @param whatAmI the initial self-description
   * @param whyDoIExist the initial purpose/reason
   * @param relationships the initial relationship network
   * @return the established narrative
   */
  ComponentNarrative establishNarrative(
      String componentId, String whatAmI, String whyDoIExist, List<String> relationships);

  /**
   * Updates the "What am I?" aspect of a component's narrative.
   *
   * @param componentId the component identifier
   * @param newDescription the updated self-description
   * @param reason the reason for the update
   */
  void updateWhatAmI(String componentId, String newDescription, String reason);

  /**
   * Updates the current purpose understanding of a component.
   *
   * <p>This allows the component's understanding of its purpose to evolve beyond its original
   * reason for creation.
   *
   * @param componentId the component identifier
   * @param newUnderstanding the evolved purpose understanding
   * @param reason the reason for the evolution
   */
  void updatePurposeUnderstanding(String componentId, String newUnderstanding, String reason);

  /**
   * Adds a relationship to a component's network.
   *
   * @param componentId the component identifier
   * @param relatedComponentId the identifier of the related component
   * @param relationshipType the type of relationship (e.g., "depends-on", "provides-to")
   */
  void addRelationship(String componentId, String relatedComponentId, String relationshipType);

  /**
   * Removes a relationship from a component's network.
   *
   * @param componentId the component identifier
   * @param relatedComponentId the identifier of the component to remove
   */
  void removeRelationship(String componentId, String relatedComponentId);

  /**
   * Gets the narrative evolution history for a component.
   *
   * @param componentId the component identifier
   * @return the list of narrative evolution events
   */
  List<String> getNarrativeEvolutionHistory(String componentId);

  /**
   * Checks if a component has an established narrative.
   *
   * @param componentId the component identifier
   * @return true if the component has a narrative
   */
  boolean hasNarrative(String componentId);
}
