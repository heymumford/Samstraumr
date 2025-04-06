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
