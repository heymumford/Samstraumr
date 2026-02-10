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

package org.s8r.application.service.template;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.template.TemplatePort;
import org.s8r.application.port.template.TemplatePort.*;

/**
 * Service layer for template rendering and document generation operations.
 *
 * <p>This service provides a simplified API for template operations by encapsulating the
 * TemplatePort interface and providing methods that handle error logging and conversion between
 * different result types.
 */
public class TemplateService {
  private final TemplatePort templatePort;
  private final LoggerPort logger;

  /**
   * Constructs a TemplateService with the given TemplatePort and LoggerPort.
   *
   * @param templatePort The TemplatePort to use
   * @param logger The LoggerPort to use for logging
   */
  public TemplateService(TemplatePort templatePort, LoggerPort logger) {
    this.templatePort = templatePort;
    this.logger = logger;
  }

  /**
   * Gets all registered template engines.
   *
   * @return A set of registered template engine names
   */
  public Set<String> getRegisteredEngines() {
    logger.debug("Getting registered template engines");
    Set<String> engines = templatePort.getRegisteredEngines();
    logger.debug("Found {} registered template engines", engines.size());
    return engines;
  }

  /**
   * Gets the default template engine.
   *
   * @return The default template engine name
   */
  public String getDefaultEngine() {
    logger.debug("Getting default template engine");
    String defaultEngine = templatePort.getDefaultEngine();
    logger.debug("Default template engine: {}", defaultEngine);
    return defaultEngine;
  }

  /**
   * Sets the default template engine.
   *
   * @param engineName The template engine name to set as default
   * @return True if the operation was successful, false otherwise
   */
  public boolean setDefaultEngine(String engineName) {
    logger.debug("Setting default template engine: {}", engineName);
    OperationResult result = templatePort.setDefaultEngine(engineName);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to set default template engine to {}: {}", engineName, result.getMessage());
      return false;
    }

    logger.debug("Default template engine set to: {}", engineName);
    return true;
  }

  /**
   * Registers a template in the template registry.
   *
   * @param templateKey The template key/name
   * @param templateContent The template content
   * @return True if the operation was successful, false otherwise
   */
  public boolean registerTemplate(String templateKey, String templateContent) {
    logger.debug("Registering template: {}", templateKey);
    TemplateResult result = templatePort.registerTemplate(templateKey, templateContent);

    if (!result.isSuccessful()) {
      logger.warn("Failed to register template {}: {}", templateKey, result.getMessage());
      return false;
    }

    logger.debug("Template registered successfully: {}", templateKey);
    return true;
  }

  /**
   * Registers a template in the template registry with a specific engine.
   *
   * @param templateKey The template key/name
   * @param templateContent The template content
   * @param engineName The engine to use for this template
   * @return True if the operation was successful, false otherwise
   */
  public boolean registerTemplate(String templateKey, String templateContent, String engineName) {
    logger.debug("Registering template with engine: {}, {}", templateKey, engineName);
    TemplateResult result = templatePort.registerTemplate(templateKey, templateContent, engineName);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to register template {} with engine {}: {}",
          templateKey,
          engineName,
          result.getMessage());
      return false;
    }

    logger.debug("Template registered successfully with engine: {}, {}", templateKey, engineName);
    return true;
  }

  /**
   * Loads a template from a file and registers it in the template registry.
   *
   * @param templateKey The template key/name
   * @param templatePath The path to the template file
   * @return True if the operation was successful, false otherwise
   */
  public boolean registerTemplateFromFile(String templateKey, Path templatePath) {
    logger.debug("Registering template from file: {}, {}", templateKey, templatePath);
    TemplateResult result = templatePort.registerTemplateFromFile(templateKey, templatePath);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to register template {} from file {}: {}",
          templateKey,
          templatePath,
          result.getMessage());
      return false;
    }

    logger.debug("Template registered successfully from file: {}, {}", templateKey, templatePath);
    return true;
  }

  /**
   * Loads a template from a file and registers it in the template registry with a specific engine.
   *
   * @param templateKey The template key/name
   * @param templatePath The path to the template file
   * @param engineName The engine to use for this template
   * @return True if the operation was successful, false otherwise
   */
  public boolean registerTemplateFromFile(
      String templateKey, Path templatePath, String engineName) {
    logger.debug(
        "Registering template from file with engine: {}, {}, {}",
        templateKey,
        templatePath,
        engineName);
    TemplateResult result =
        templatePort.registerTemplateFromFile(templateKey, templatePath, engineName);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to register template {} from file {} with engine {}: {}",
          templateKey,
          templatePath,
          engineName,
          result.getMessage());
      return false;
    }

    logger.debug(
        "Template registered successfully from file with engine: {}, {}, {}",
        templateKey,
        templatePath,
        engineName);
    return true;
  }

  /**
   * Loads and registers all templates from a directory.
   *
   * @param directory The directory containing templates
   * @param filePattern The pattern to match template files (e.g., "*.ftl", "*.vm")
   * @return A map of successfully registered template keys to their template info, empty if
   *     operation failed
   */
  public Map<String, TemplateInfo> registerTemplatesFromDirectory(
      Path directory, String filePattern) {
    logger.debug("Registering templates from directory: {}, pattern: {}", directory, filePattern);
    TemplateRegistrationResult result =
        templatePort.registerTemplatesFromDirectory(directory, filePattern);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to register templates from directory {}: {}", directory, result.getMessage());
      return Collections.emptyMap();
    }

    if (result.getFailedCount() > 0) {
      logger.warn(
          "Some templates failed to register: {} succeeded, {} failed",
          result.getSuccessfulCount(),
          result.getFailedCount());

      for (Map.Entry<String, String> entry : result.getFailedTemplates().entrySet()) {
        logger.warn("Failed to register template {}: {}", entry.getKey(), entry.getValue());
      }
    }

    logger.debug(
        "Successfully registered {} templates from directory {}",
        result.getSuccessfulCount(),
        directory);
    return result.getTemplates();
  }

  /**
   * Loads and registers all templates from a directory with a specific engine.
   *
   * @param directory The directory containing templates
   * @param filePattern The pattern to match template files (e.g., "*.ftl", "*.vm")
   * @param engineName The engine to use for these templates
   * @return A map of successfully registered template keys to their template info, empty if
   *     operation failed
   */
  public Map<String, TemplateInfo> registerTemplatesFromDirectory(
      Path directory, String filePattern, String engineName) {
    logger.debug(
        "Registering templates from directory with engine: {}, pattern: {}, engine: {}",
        directory,
        filePattern,
        engineName);
    TemplateRegistrationResult result =
        templatePort.registerTemplatesFromDirectory(directory, filePattern, engineName);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to register templates from directory {} with engine {}: {}",
          directory,
          engineName,
          result.getMessage());
      return Collections.emptyMap();
    }

    if (result.getFailedCount() > 0) {
      logger.warn(
          "Some templates failed to register: {} succeeded, {} failed",
          result.getSuccessfulCount(),
          result.getFailedCount());

      for (Map.Entry<String, String> entry : result.getFailedTemplates().entrySet()) {
        logger.warn("Failed to register template {}: {}", entry.getKey(), entry.getValue());
      }
    }

    logger.debug(
        "Successfully registered {} templates from directory {} with engine {}",
        result.getSuccessfulCount(),
        directory,
        engineName);
    return result.getTemplates();
  }

  /**
   * Checks if a template is registered.
   *
   * @param templateKey The template key/name
   * @return True if the template is registered, false otherwise
   */
  public boolean hasTemplate(String templateKey) {
    logger.debug("Checking if template exists: {}", templateKey);
    boolean exists = templatePort.hasTemplate(templateKey);
    logger.debug("Template {} {}", templateKey, exists ? "exists" : "does not exist");
    return exists;
  }

  /**
   * Gets a registered template.
   *
   * @param templateKey The template key/name
   * @return An Optional containing the template info if found, empty otherwise
   */
  public Optional<TemplateInfo> getTemplate(String templateKey) {
    logger.debug("Getting template: {}", templateKey);
    TemplateResult result = templatePort.getTemplate(templateKey);

    if (!result.isSuccessful()) {
      logger.warn("Failed to get template {}: {}", templateKey, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Template retrieved successfully: {}", templateKey);
    return result.getTemplate();
  }

  /**
   * Removes a template from the registry.
   *
   * @param templateKey The template key/name
   * @return True if the operation was successful, false otherwise
   */
  public boolean removeTemplate(String templateKey) {
    logger.debug("Removing template: {}", templateKey);
    OperationResult result = templatePort.removeTemplate(templateKey);

    if (!result.isSuccessful()) {
      logger.warn("Failed to remove template {}: {}", templateKey, result.getMessage());
      return false;
    }

    logger.debug("Template removed successfully: {}", templateKey);
    return true;
  }

  /**
   * Gets the engine for a registered template.
   *
   * @param templateKey The template key/name
   * @return The engine name if the template is registered, empty otherwise
   */
  public Optional<String> getTemplateEngine(String templateKey) {
    logger.debug("Getting engine for template: {}", templateKey);
    Optional<String> engine = templatePort.getTemplateEngine(templateKey);

    if (engine.isPresent()) {
      logger.debug("Engine for template {}: {}", templateKey, engine.get());
    } else {
      logger.debug("No engine found for template {}", templateKey);
    }

    return engine;
  }

  /**
   * Renders a template with the provided data model.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @return An Optional containing the rendered content if successful, empty otherwise
   */
  public Optional<String> renderTemplate(String templateKey, Map<String, Object> dataModel) {
    logger.debug("Rendering template: {}", templateKey);
    RenderResult result = templatePort.renderTemplate(templateKey, dataModel);

    if (!result.isSuccessful()) {
      logger.warn("Failed to render template {}: {}", templateKey, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Template rendered successfully: {}", templateKey);
    return result.getRenderedContent();
  }

  /**
   * Renders a template with the provided data model using a specific engine.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param engineName The engine to use for rendering
   * @return An Optional containing the rendered content if successful, empty otherwise
   */
  public Optional<String> renderTemplate(
      String templateKey, Map<String, Object> dataModel, String engineName) {
    logger.debug("Rendering template with engine: {}, {}", templateKey, engineName);
    RenderResult result = templatePort.renderTemplate(templateKey, dataModel, engineName);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to render template {} with engine {}: {}",
          templateKey,
          engineName,
          result.getMessage());
      return Optional.empty();
    }

    logger.debug("Template rendered successfully with engine: {}, {}", templateKey, engineName);
    return result.getRenderedContent();
  }

  /**
   * Renders a template string directly with the provided data model.
   *
   * @param templateContent The template content
   * @param dataModel The data model
   * @return An Optional containing the rendered content if successful, empty otherwise
   */
  public Optional<String> renderString(String templateContent, Map<String, Object> dataModel) {
    logger.debug("Rendering template string");
    RenderResult result = templatePort.renderString(templateContent, dataModel);

    if (!result.isSuccessful()) {
      logger.warn("Failed to render template string: {}", result.getMessage());
      return Optional.empty();
    }

    logger.debug("Template string rendered successfully");
    return result.getRenderedContent();
  }

  /**
   * Renders a template string directly with the provided data model using a specific engine.
   *
   * @param templateContent The template content
   * @param dataModel The data model
   * @param engineName The engine to use for rendering
   * @return An Optional containing the rendered content if successful, empty otherwise
   */
  public Optional<String> renderString(
      String templateContent, Map<String, Object> dataModel, String engineName) {
    logger.debug("Rendering template string with engine: {}", engineName);
    RenderResult result = templatePort.renderString(templateContent, dataModel, engineName);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to render template string with engine {}: {}", engineName, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Template string rendered successfully with engine: {}", engineName);
    return result.getRenderedContent();
  }

  /**
   * Generates a PDF document from a rendered HTML template.
   *
   * @param html The HTML content
   * @param outputPath The path where to save the PDF
   * @return True if the operation was successful, false otherwise
   */
  public boolean generatePdf(String html, Path outputPath) {
    logger.debug("Generating PDF to: {}", outputPath);
    DocumentResult result = templatePort.generatePdf(html, outputPath);

    if (!result.isSuccessful()) {
      logger.warn("Failed to generate PDF to {}: {}", outputPath, result.getMessage());
      return false;
    }

    logger.debug(
        "PDF generated successfully to {}, size: {} bytes",
        outputPath,
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a PDF document from a rendered HTML template with options.
   *
   * @param html The HTML content
   * @param outputPath The path where to save the PDF
   * @param options The PDF generation options
   * @return True if the operation was successful, false otherwise
   */
  public boolean generatePdf(String html, Path outputPath, PdfOptions options) {
    logger.debug("Generating PDF with options to: {}", outputPath);
    DocumentResult result = templatePort.generatePdf(html, outputPath, options);

    if (!result.isSuccessful()) {
      logger.warn("Failed to generate PDF with options to {}: {}", outputPath, result.getMessage());
      return false;
    }

    logger.debug(
        "PDF generated successfully with options to {}, size: {} bytes",
        outputPath,
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a PDF document from a rendered HTML template and writes it to an output stream.
   *
   * @param html The HTML content
   * @param outputStream The output stream to write the PDF to
   * @return True if the operation was successful, false otherwise
   */
  public boolean generatePdf(String html, OutputStream outputStream) {
    logger.debug("Generating PDF to output stream");
    DocumentResult result = templatePort.generatePdf(html, outputStream);

    if (!result.isSuccessful()) {
      logger.warn("Failed to generate PDF to output stream: {}", result.getMessage());
      return false;
    }

    logger.debug(
        "PDF generated successfully to output stream, size: {} bytes",
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a PDF document from a rendered HTML template with options and writes it to an output
   * stream.
   *
   * @param html The HTML content
   * @param outputStream The output stream to write the PDF to
   * @param options The PDF generation options
   * @return True if the operation was successful, false otherwise
   */
  public boolean generatePdf(String html, OutputStream outputStream, PdfOptions options) {
    logger.debug("Generating PDF with options to output stream");
    DocumentResult result = templatePort.generatePdf(html, outputStream, options);

    if (!result.isSuccessful()) {
      logger.warn("Failed to generate PDF with options to output stream: {}", result.getMessage());
      return false;
    }

    logger.debug(
        "PDF generated successfully with options to output stream, size: {} bytes",
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a document from a template and data model.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputPath The path where to save the document
   * @return True if the operation was successful, false otherwise
   */
  public boolean generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      Path outputPath) {
    logger.debug(
        "Generating {} document from template {} to: {}", outputFormat, templateKey, outputPath);
    DocumentResult result =
        templatePort.generateDocument(templateKey, dataModel, outputFormat, outputPath);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to generate {} document from template {} to {}: {}",
          outputFormat,
          templateKey,
          outputPath,
          result.getMessage());
      return false;
    }

    logger.debug(
        "{} document generated successfully from template {} to {}, size: {} bytes",
        outputFormat,
        templateKey,
        outputPath,
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a document from a template and data model with options.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputPath The path where to save the document
   * @param options The document generation options
   * @return True if the operation was successful, false otherwise
   */
  public boolean generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      Path outputPath,
      DocumentOptions options) {
    logger.debug(
        "Generating {} document with options from template {} to: {}",
        outputFormat,
        templateKey,
        outputPath);
    DocumentResult result =
        templatePort.generateDocument(templateKey, dataModel, outputFormat, outputPath, options);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to generate {} document with options from template {} to {}: {}",
          outputFormat,
          templateKey,
          outputPath,
          result.getMessage());
      return false;
    }

    logger.debug(
        "{} document generated successfully with options from template {} to {}, size: {} bytes",
        outputFormat,
        templateKey,
        outputPath,
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a document from a template and data model and writes it to an output stream.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputStream The output stream to write the document to
   * @return True if the operation was successful, false otherwise
   */
  public boolean generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      OutputStream outputStream) {
    logger.debug(
        "Generating {} document from template {} to output stream", outputFormat, templateKey);
    DocumentResult result =
        templatePort.generateDocument(templateKey, dataModel, outputFormat, outputStream);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to generate {} document from template {} to output stream: {}",
          outputFormat,
          templateKey,
          result.getMessage());
      return false;
    }

    logger.debug(
        "{} document generated successfully from template {} to output stream, size: {} bytes",
        outputFormat,
        templateKey,
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Generates a document from a template and data model with options and writes it to an output
   * stream.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputStream The output stream to write the document to
   * @param options The document generation options
   * @return True if the operation was successful, false otherwise
   */
  public boolean generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      OutputStream outputStream,
      DocumentOptions options) {
    logger.debug(
        "Generating {} document with options from template {} to output stream",
        outputFormat,
        templateKey);
    DocumentResult result =
        templatePort.generateDocument(templateKey, dataModel, outputFormat, outputStream, options);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to generate {} document with options from template {} to output stream: {}",
          outputFormat,
          templateKey,
          result.getMessage());
      return false;
    }

    logger.debug(
        "{} document generated successfully with options from template {} to output stream, size: {} bytes",
        outputFormat,
        templateKey,
        result.getDocumentSize().orElse(0L));
    return true;
  }

  /**
   * Creates a PdfOptions builder.
   *
   * @return A new PdfOptions.Builder
   */
  public PdfOptions.Builder pdfOptionsBuilder() {
    return new PdfOptions.Builder() {
      private PageSize pageSize = PageSize.A4;
      private PageOrientation orientation = PageOrientation.PORTRAIT;
      private Margins margins = createMargins(72, 72, 72, 72); // 1 inch margins
      private boolean compressed = true;
      private boolean pdfA = false;
      private boolean includeTableOfContents = false;
      private String baseUrl;
      private final Map<String, String> metadata = new HashMap<>();

      @Override
      public PdfOptions.Builder pageSize(PageSize pageSize) {
        this.pageSize = pageSize;
        return this;
      }

      @Override
      public PdfOptions.Builder orientation(PageOrientation orientation) {
        this.orientation = orientation;
        return this;
      }

      @Override
      public PdfOptions.Builder margins(Margins margins) {
        this.margins = margins;
        return this;
      }

      @Override
      public PdfOptions.Builder compressed(boolean compressed) {
        this.compressed = compressed;
        return this;
      }

      @Override
      public PdfOptions.Builder pdfA(boolean pdfA) {
        this.pdfA = pdfA;
        return this;
      }

      @Override
      public PdfOptions.Builder includeTableOfContents(boolean includeTableOfContents) {
        this.includeTableOfContents = includeTableOfContents;
        return this;
      }

      @Override
      public PdfOptions.Builder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
      }

      @Override
      public PdfOptions.Builder metadata(Map<String, String> metadata) {
        this.metadata.clear();
        if (metadata != null) {
          this.metadata.putAll(metadata);
        }
        return this;
      }

      @Override
      public PdfOptions build() {
        return new PdfOptions() {
          @Override
          public PageSize getPageSize() {
            return pageSize;
          }

          @Override
          public PageOrientation getOrientation() {
            return orientation;
          }

          @Override
          public Margins getMargins() {
            return margins;
          }

          @Override
          public boolean isCompressed() {
            return compressed;
          }

          @Override
          public boolean isPdfA() {
            return pdfA;
          }

          @Override
          public boolean includeTableOfContents() {
            return includeTableOfContents;
          }

          @Override
          public Optional<String> getBaseUrl() {
            return Optional.ofNullable(baseUrl);
          }

          @Override
          public Map<String, String> getMetadata() {
            return Collections.unmodifiableMap(metadata);
          }
        };
      }
    };
  }

  /**
   * Creates margins for PDF documents.
   *
   * @param top The top margin in points
   * @param right The right margin in points
   * @param bottom The bottom margin in points
   * @param left The left margin in points
   * @return The created Margins
   */
  public Margins createMargins(float top, float right, float bottom, float left) {
    return new Margins() {
      @Override
      public float getTop() {
        return top;
      }

      @Override
      public float getRight() {
        return right;
      }

      @Override
      public float getBottom() {
        return bottom;
      }

      @Override
      public float getLeft() {
        return left;
      }
    };
  }

  /**
   * Creates simple document options with metadata.
   *
   * @param metadata The document metadata
   * @return The created DocumentOptions
   */
  public DocumentOptions createDocumentOptions(Map<String, String> metadata) {
    return () -> Collections.unmodifiableMap(new HashMap<>(metadata));
  }

  /**
   * Renders a template and saves it to a file.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputPath The path where to save the rendered content
   * @return True if the operation was successful, false otherwise
   */
  public boolean renderAndSaveToFile(
      String templateKey, Map<String, Object> dataModel, Path outputPath) {
    logger.debug("Rendering template {} and saving to file: {}", templateKey, outputPath);

    Optional<String> renderedContent = renderTemplate(templateKey, dataModel);
    if (renderedContent.isEmpty()) {
      return false;
    }

    try {
      Files.createDirectories(outputPath.getParent());
      Files.writeString(outputPath, renderedContent.get());
      logger.debug("Rendered content saved to file: {}", outputPath);
      return true;
    } catch (IOException e) {
      logger.error("Failed to save rendered content to file {}: {}", outputPath, e.getMessage(), e);
      return false;
    }
  }

  /**
   * Renders a template asynchronously.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @return A CompletableFuture that will contain the rendered content if successful, or will
   *     complete exceptionally if unsuccessful
   */
  public CompletableFuture<String> renderTemplateAsync(
      String templateKey, Map<String, Object> dataModel) {
    return CompletableFuture.supplyAsync(
        () -> {
          Optional<String> renderedContent = renderTemplate(templateKey, dataModel);
          return renderedContent.orElseThrow(
              () -> new RuntimeException("Failed to render template: " + templateKey));
        });
  }

  /**
   * Generates a document asynchronously.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputPath The path where to save the document
   * @return A CompletableFuture that will complete successfully if the document was generated, or
   *     will complete exceptionally if unsuccessful
   */
  public CompletableFuture<Path> generateDocumentAsync(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      Path outputPath) {
    return CompletableFuture.supplyAsync(
        () -> {
          boolean success = generateDocument(templateKey, dataModel, outputFormat, outputPath);
          if (!success) {
            throw new RuntimeException(
                "Failed to generate " + outputFormat + " document from template: " + templateKey);
          }
          return outputPath;
        });
  }
}
