/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.tube;

/**
 * A utility class for inspecting TubeIdentity instances.
 *
 * <p>This class is intended for debugging and testing, to show how TubeIdentity objects are
 * constructed and what they contain in memory.
 */
public class TubeIdentityInspector {

  /**
   * Prints details about a TubeIdentity object.
   *
   * @param identity The TubeIdentity to inspect
   * @return A string containing detailed information about the identity
   */
  public static String inspect(TubeIdentity identity) {
    if (identity == null) {
      return "TubeIdentity is null";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("TubeIdentity Inspection:\n");
    sb.append("---------------------\n");
    sb.append("- Unique ID: ").append(identity.getUniqueId()).append("\n");
    sb.append("- Reason: ").append(identity.getReason()).append("\n");
    sb.append("- Conception Time: ").append(identity.getConceptionTime()).append("\n");
    sb.append("- Is Adam Tube: ").append(identity.isAdamTube()).append("\n");
    sb.append("- Hierarchical Address: ").append(identity.getHierarchicalAddress()).append("\n");

    // Parent info
    if (identity.getParentIdentity() != null) {
      sb.append("- Parent ID: ").append(identity.getParentIdentity().getUniqueId()).append("\n");
    } else {
      sb.append("- Parent ID: null (This is an Adam tube)\n");
    }

    // Descendants
    sb.append("- Descendants Count: ").append(identity.getDescendants().size()).append("\n");
    if (!identity.getDescendants().isEmpty()) {
      sb.append("- Descendant IDs:\n");
      for (TubeIdentity child : identity.getDescendants()) {
        sb.append("  * ").append(child.getUniqueId()).append("\n");
      }
    }

    // Environmental context
    sb.append("- Environment Context:\n");
    if (identity.getEnvironmentContext().isEmpty()) {
      sb.append("  (empty)\n");
    } else {
      for (String key : identity.getEnvironmentContext().keySet()) {
        sb.append("  * ")
            .append(key)
            .append(": ")
            .append(identity.getEnvironmentContext().get(key))
            .append("\n");
      }
    }

    // Lineage
    sb.append("- Lineage Entries: ").append(identity.getLineage().size()).append("\n");
    if (!identity.getLineage().isEmpty()) {
      sb.append("- Lineage Content:\n");
      for (String entry : identity.getLineage()) {
        sb.append("  * ").append(entry).append("\n");
      }
    }

    return sb.toString();
  }

  /**
   * Inspects a Tube object's identity-related properties.
   *
   * @param tube The Tube to inspect
   * @return A string containing tube identity information
   */
  public static String inspectTube(Tube tube) {
    if (tube == null) {
      return "Tube is null";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Tube Identity-Related Properties:\n");
    sb.append("-------------------------------\n");
    sb.append("- Unique ID: ").append(tube.getUniqueId()).append("\n");
    sb.append("- Reason: ").append(tube.getReason()).append("\n");
    sb.append("- Status: ").append(tube.getStatus()).append("\n");

    // Lineage
    sb.append("- Lineage Entries: ").append(tube.getLineage().size()).append("\n");
    if (!tube.getLineage().isEmpty()) {
      sb.append("- Lineage Content:\n");
      for (String entry : tube.getLineage()) {
        sb.append("  * ").append(entry).append("\n");
      }
    }

    // Mimir log
    sb.append("- Mimir Log Size: ").append(tube.getMimirLogSize()).append("\n");

    return sb.toString();
  }

  /**
   * Demonstrates the creation and inspection of various identity types. This method can be invoked
   * to see examples of identities in memory.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    System.out.println("===== Tube Identity Inspection Demo =====\n");

    // Create environment
    Environment env = new Environment();
    System.out.println("Environment created with ID: " + env.getEnvironmentId());

    // Create an Adam tube identity
    TubeIdentity adamId = TubeIdentity.createAdamIdentity("Adam Tube Demo", env);
    System.out.println("\n" + inspect(adamId));

    // Create a child tube identity
    TubeIdentity childId = TubeIdentity.createChildIdentity("Child Tube Demo", env, adamId);
    adamId.addChild(childId);
    System.out.println("\n" + inspect(childId));

    // Create a second-generation child
    TubeIdentity grandchildId =
        TubeIdentity.createChildIdentity("Grandchild Tube Demo", env, childId);
    childId.addChild(grandchildId);
    System.out.println("\n" + inspect(grandchildId));

    // Create an actual tube and show its identity-related properties
    Tube tube = Tube.create("Tube Demo", env);
    System.out.println("\n" + inspectTube(tube));
  }
}
