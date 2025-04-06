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

package org.s8r.tube.reporting;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to generate documentation using Docmosis. Uses reflection to load Docmosis classes
 * dynamically to avoid direct dependencies.
 */
public class DocumentGenerator {
  private static final Logger LOGGER = Logger.getLogger(DocumentGenerator.class.getName());
  private static final String DEFAULT_OUTPUT_DIR = "target/docs";
  private static final String DEFAULT_FORMAT = "pdf";
  private static final String TEMPLATES_DIR = "templates";

  private static final String[] SUPPORTED_FORMATS = {"pdf", "docx", "html"};

  /**
   * Main method for command line usage. Arguments: [0] - Output directory (optional, default is
   * "target/docs") [1] - Output format (optional, default is "pdf")
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    String outputDir = DEFAULT_OUTPUT_DIR;
    String format = DEFAULT_FORMAT;

    if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
      outputDir = args[0];
    }

    if (args.length > 1 && args[1] != null && !args[1].trim().isEmpty()) {
      format = args[1].toLowerCase();
      if (!Arrays.asList(SUPPORTED_FORMATS).contains(format)) {
        System.err.println("Unsupported format: " + format);
        System.err.println("Supported formats: " + String.join(", ", SUPPORTED_FORMATS));
        System.exit(1);
      }
    }

    try {
      generateDocumentation(outputDir, format);
      System.out.println("Documentation generated successfully in " + outputDir);
      System.exit(0);
    } catch (Exception e) {
      System.err.println("Error generating documentation: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Generate documentation using Docmosis in the specified format.
   *
   * @param outputDir Output directory for the generated documents
   * @param format Output format (pdf, docx, html)
   * @throws Exception If an error occurs during document generation
   */
  public static void generateDocumentation(String outputDir, String format) throws Exception {
    // Ensure output directory exists
    File outDir = new File(outputDir);
    if (!outDir.exists()) {
      if (!outDir.mkdirs()) {
        throw new RuntimeException("Failed to create output directory: " + outputDir);
      }
    }

    // Get Docmosis license key from environment or system property
    String docmosisKey = System.getenv("DOCMOSIS_KEY");
    if (docmosisKey == null || docmosisKey.trim().isEmpty()) {
      docmosisKey = System.getProperty("docmosis.key");
    }

    // Get Docmosis site name from environment or system property
    String docmosisSite = System.getenv("DOCMOSIS_SITE");
    if (docmosisSite == null || docmosisSite.trim().isEmpty()) {
      docmosisSite = System.getProperty("docmosis.site");
    }

    if (docmosisKey == null || docmosisKey.trim().isEmpty()) {
      throw new RuntimeException(
          "Docmosis license key not provided. Set DOCMOSIS_KEY environment variable or docmosis.key system property.");
    }

    if (docmosisSite == null || docmosisSite.trim().isEmpty()) {
      docmosisSite = "Free Trial Java";
    }

    // Find template files
    String templatesDirPath = getTemplatesDirectory();
    File templatesDir = new File(templatesDirPath);
    if (!templatesDir.exists() || !templatesDir.isDirectory()) {
      // Check if this is a test environment
      String testTemplatesDir = System.getProperty("templates.dir");
      if (testTemplatesDir != null && !testTemplatesDir.isEmpty()) {
        templatesDir = new File(testTemplatesDir);
        if (templatesDir.exists() && templatesDir.isDirectory()) {
          LOGGER.info("Using test templates directory: " + testTemplatesDir);
        } else {
          throw new RuntimeException("Test templates directory not found: " + testTemplatesDir);
        }
      } else {
        throw new RuntimeException("Templates directory not found: " + templatesDirPath);
      }
    }

    File[] templateFiles =
        templatesDir.listFiles(
            new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".docx");
              }
            });

    if (templateFiles == null || templateFiles.length == 0) {
      throw new RuntimeException("No template files found in: " + templatesDirPath);
    }

    try {
      // Initialize Docmosis using reflection
      Class<?> systemManagerClass = Class.forName("com.docmosis.SystemManager");

      // Initialize Docmosis
      Method initializeMethod = systemManagerClass.getMethod("initialise");
      initializeMethod.invoke(null);

      // Set license key
      Method setKeyMethod = systemManagerClass.getMethod("setKey", String.class);
      setKeyMethod.invoke(null, docmosisKey);

      // Set site
      Method setSiteMethod = systemManagerClass.getMethod("setSite", String.class);
      setSiteMethod.invoke(null, docmosisSite);

      // Get renderer
      Method getRendererMethod = systemManagerClass.getMethod("getRenderer");
      Object renderer = getRendererMethod.invoke(null);
      Class<?> rendererClass = renderer.getClass();
    } catch (ClassNotFoundException e) {
      LOGGER.warning(
          "Docmosis library not found. Using fallback mechanism for document generation.");
      generateFallbackDocumentation(outputDir, format, templateFiles);
      return;
    } catch (ReflectiveOperationException e) {
      LOGGER.warning("Error accessing Docmosis API: " + e.getMessage());
      generateFallbackDocumentation(outputDir, format, templateFiles);
      return;
    }

    // Get current timestamp
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String timestamp = now.format(formatter);

    try {
      // Process each template
      for (File templateFile : templateFiles) {
        String templateName = templateFile.getName();
        String baseName = templateName.substring(0, templateName.lastIndexOf('.'));

        // Create data for template
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", timestamp);
        data.put("version", getProjectVersion());
        data.put("generationDate", now.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        data.put("projectName", "Samstraumr");

        // Output filename
        String outputFileName = baseName + "." + format;
        String outputPath = Paths.get(outputDir, outputFileName).toString();

        try {
          // Render document with reflection (compatible with Java 21's stronger module boundaries)
          Class<?> systemManagerClass = Class.forName("com.docmosis.SystemManager");
          Method getRendererMethod = systemManagerClass.getMethod("getRenderer");
          Object renderer = getRendererMethod.invoke(null);
          Class<?> rendererClass = renderer.getClass();

          Method renderMethod =
              rendererClass.getMethod("render", String.class, String.class, Map.class);
          renderMethod.invoke(renderer, templateFile.getAbsolutePath(), outputPath, data);

          LOGGER.info("Generated document: " + outputPath);
        } catch (ReflectiveOperationException e) {
          LOGGER.warning("Error rendering document: " + e.getMessage());
          throw e;
        }
      }

      // Shutdown Docmosis
      try {
        Class<?> systemManagerClass = Class.forName("com.docmosis.SystemManager");
        Method shutdownMethod = systemManagerClass.getMethod("shutdown");
        shutdownMethod.invoke(null);
      } catch (ReflectiveOperationException e) {
        LOGGER.warning("Error shutting down Docmosis: " + e.getMessage());
      }
    } catch (Exception e) {
      LOGGER.warning("Error in document generation process. Falling back to simpler format.");
      generateFallbackDocumentation(outputDir, format, templateFiles);
    }
  }

  /**
   * Get the templates directory path.
   *
   * @return Path to templates directory
   */
  private static String getTemplatesDirectory() {
    // Try to find templates in the classpath
    String templatesPath =
        DocumentGenerator.class.getClassLoader().getResource(TEMPLATES_DIR) != null
            ? DocumentGenerator.class.getClassLoader().getResource(TEMPLATES_DIR).getPath()
            : null;

    if (templatesPath != null) {
      return templatesPath;
    }

    // Try to find templates relative to working directory
    Path path = Paths.get("src/main/resources", TEMPLATES_DIR);
    if (path.toFile().exists()) {
      return path.toString();
    }

    // Try one level up
    path = Paths.get("../src/main/resources", TEMPLATES_DIR);
    if (path.toFile().exists()) {
      return path.toString();
    }

    // Default to current directory/templates
    return TEMPLATES_DIR;
  }

  /**
   * Fallback method for document generation when Docmosis is not available. Creates simple text
   * files with the same data.
   *
   * @param outputDir Output directory
   * @param format Output format
   * @param templateFiles Template files
   * @throws Exception If an error occurs
   */
  private static void generateFallbackDocumentation(
      String outputDir, String format, File[] templateFiles) throws Exception {
    LOGGER.info("Using fallback document generation mechanism");

    // Get current timestamp
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String timestamp = now.format(formatter);

    // Process each template
    for (File templateFile : templateFiles) {
      String templateName = templateFile.getName();
      String baseName = templateName.substring(0, templateName.lastIndexOf('.'));

      // Use txt format for fallback
      String outputFileName = baseName + ".txt";
      Path outputPath = Paths.get(outputDir, outputFileName);

      StringBuilder content = new StringBuilder();
      content.append("DOCUMENT GENERATION FALLBACK\n");
      content.append("==========================\n\n");
      content.append("Template: ").append(templateName).append("\n");
      content.append("Timestamp: ").append(timestamp).append("\n");
      content.append("Version: ").append(getProjectVersion()).append("\n");
      content
          .append("Generation Date: ")
          .append(now.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
          .append("\n");
      content.append("Project Name: Samstraumr\n\n");
      content.append(
          "NOTE: This is a fallback document generated because Docmosis was not available.\n");
      content.append(
          "To generate proper documentation, ensure the Docmosis library is available in the classpath.\n");

      // Write to file
      java.nio.file.Files.write(outputPath, content.toString().getBytes());

      LOGGER.info("Generated fallback document: " + outputPath);
    }
  }

  /**
   * Get the current project version.
   *
   * @return Project version string
   */
  private static String getProjectVersion() {
    // First try to get version from system property
    String version = System.getProperty("project.version");
    if (version != null && !version.trim().isEmpty()) {
      return version;
    }

    // Check if the version.properties file exists
    try {
      Path versionFile = Paths.get("Samstraumr", "version.properties");
      if (versionFile.toFile().exists()) {
        Map<String, String> props = new HashMap<>();
        java.io.FileInputStream fis = new java.io.FileInputStream(versionFile.toFile());
        java.util.Properties properties = new java.util.Properties();
        properties.load(fis);
        fis.close();
        properties.forEach((k, v) -> props.put(k.toString(), v.toString()));
        version = props.get("version");
        if (version != null) {
          return version;
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error reading version.properties: " + e.getMessage());
    }

    // Default to unknown version
    return "UNKNOWN";
  }
}
