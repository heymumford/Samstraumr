package org.s8r.core.tube.identity;

/**
 * A utility class for inspecting Identity instances.
 *
 * <p>This class is part of the simplified package structure, replacing the more specific
 * TubeIdentityInspector with a more general IdentityInspector in the s8r.core.tube.identity package.
 *
 * <p>It is intended for debugging and testing, to show how Identity objects are
 * constructed and what they contain in memory.
 */
public class IdentityInspector {

  /**
   * Prints details about an Identity object.
   *
   * @param identity The Identity to inspect
   * @return A string containing detailed information about the identity
   */
  public static String inspect(Identity identity) {
    if (identity == null) {
      return "Identity is null";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Identity Inspection:\n");
    sb.append("-------------------\n");
    sb.append("- Unique ID: ").append(identity.getUniqueId()).append("\n");
    sb.append("- Reason: ").append(identity.getReason()).append("\n");
    sb.append("- Conception Time: ").append(identity.getConceptionTime()).append("\n");
    sb.append("- Is Adam Component: ").append(identity.isAdamComponent()).append("\n");
    sb.append("- Hierarchical Address: ").append(identity.getHierarchicalAddress()).append("\n");

    // Parent info
    if (identity.getParentIdentity() != null) {
      sb.append("- Parent ID: ").append(identity.getParentIdentity().getUniqueId()).append("\n");
    } else {
      sb.append("- Parent ID: null (This is an Adam component)\n");
    }

    // Descendants
    sb.append("- Descendants Count: ").append(identity.getDescendants().size()).append("\n");
    if (!identity.getDescendants().isEmpty()) {
      sb.append("- Descendant IDs:\n");
      for (Identity child : identity.getDescendants()) {
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
   * Demonstrates the creation and inspection of various identity types. This method can be invoked
   * to see examples of identities in memory.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    System.out.println("===== Identity Inspection Demo =====\n");

    // Create environment
    org.s8r.core.env.Environment env = new org.s8r.core.env.Environment();
    System.out.println("Environment created with ID: " + env.getEnvironmentId());

    // Create an Adam component identity
    Identity adamId = Identity.createAdamIdentity("Adam Component Demo", env);
    System.out.println("\n" + inspect(adamId));

    // Create a child component identity
    Identity childId = Identity.createChildIdentity("Child Component Demo", env, adamId);
    adamId.addChild(childId);
    System.out.println("\n" + inspect(childId));

    // Create a second-generation child
    Identity grandchildId =
        Identity.createChildIdentity("Grandchild Component Demo", env, childId);
    childId.addChild(grandchildId);
    System.out.println("\n" + inspect(grandchildId));
  }
}