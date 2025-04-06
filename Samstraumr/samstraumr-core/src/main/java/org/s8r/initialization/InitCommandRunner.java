/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.initialization;

/**
 * Command-line runner for the S8rInitializer. This class provides a simple entry point for
 * initializing a Samstraumr repository.
 */
public class InitCommandRunner {

  /**
   * Main entry point for the initializer.
   *
   * @param args Command line arguments. args[0] should be the repository path, and args[1]
   *     (optional) should be the package name.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println(
          "Usage: java org.s8r.initialization.InitCommandRunner <repo-path> [package-name]");
      System.exit(1);
    }

    String repoPath = args[0];
    String packageName = args.length > 1 ? args[1] : null;

    S8rInitializer initializer;
    if (packageName != null) {
      initializer = new S8rInitializer(repoPath, packageName);
    } else {
      initializer = new S8rInitializer(repoPath);
    }

    boolean success = initializer.initialize();

    if (!success) {
      System.exit(1);
    }
  }
}
