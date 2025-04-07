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

package org.s8r.infrastructure.template;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.template.TemplatePort;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the TemplatePort interface using Freemarker as the primary template engine,
 * with support for PDF generation.
 *
 * <p>This adapter provides a template engine implementation using Freemarker for template
 * processing and an HTML-to-PDF library for PDF generation. It manages a registry of templates and
 * supports multiple template engines, with Freemarker as the default.
 */
public class FreemarkerTemplateAdapter implements TemplatePort {
  private static final String FREEMARKER_ENGINE = "freemarker";
  private static final String VELOCITY_ENGINE = "velocity";
  private static final String THYMELEAF_ENGINE = "thymeleaf";
  private static final Set<String> SUPPORTED_ENGINES = 
      Set.of(FREEMARKER_ENGINE, VELOCITY_ENGINE, THYMELEAF_ENGINE);
  
  private String defaultEngine = FREEMARKER_ENGINE;
  private final Map<String, TemplateInfo> templateRegistry = new ConcurrentHashMap<>();
  private final LoggerPort logger;
  
  // For simplicity, we're using a mock implementation without actual dependencies
  // In a real implementation, we would include the actual libraries like freemarker, itext, etc.

  /**
   * Constructs a FreemarkerTemplateAdapter with the given LoggerPort.
   *
   * @param logger The LoggerPort to use for logging
   */
  public FreemarkerTemplateAdapter(LoggerPort logger) {
    this.logger = logger;
    logger.info("FreemarkerTemplateAdapter initialized with supported engines: {}", 
        String.join(", ", SUPPORTED_ENGINES));
  }

  @Override
  public Set<String> getRegisteredEngines() {
    return new HashSet<>(SUPPORTED_ENGINES);
  }

  @Override
  public String getDefaultEngine() {
    return defaultEngine;
  }

  @Override
  public OperationResult setDefaultEngine(String engineName) {
    if (!SUPPORTED_ENGINES.contains(engineName)) {
      logger.warn("Attempted to set unsupported engine as default: {}", engineName);
      return OperationResult.failure(
          "Unsupported template engine: " + engineName, "UNSUPPORTED_ENGINE");
    }
    
    this.defaultEngine = engineName;
    logger.info("Default template engine set to: {}", engineName);
    return OperationResult.success("Default engine set to: " + engineName);
  }

  @Override
  public TemplateResult registerTemplate(String templateKey, String templateContent) {
    return registerTemplate(templateKey, templateContent, defaultEngine);
  }

  @Override
  public TemplateResult registerTemplate(String templateKey, String templateContent, String engineName) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to register template with null or empty key");
      return TemplateResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    if (templateContent == null) {
      logger.warn("Attempted to register template with null content: {}", templateKey);
      return TemplateResult.failure(
          "Template content cannot be null", "INVALID_CONTENT");
    }
    
    if (!SUPPORTED_ENGINES.contains(engineName)) {
      logger.warn("Attempted to register template with unsupported engine: {}, {}", 
          templateKey, engineName);
      return TemplateResult.failure(
          "Unsupported template engine: " + engineName, "UNSUPPORTED_ENGINE");
    }
    
    // Validate the template syntax based on the engine
    try {
      validateTemplateSyntax(templateContent, engineName);
    } catch (Exception e) {
      logger.warn("Template syntax validation failed for {}: {}", templateKey, e.getMessage());
      return TemplateResult.failure(
          "Template syntax validation failed: " + e.getMessage(), "INVALID_SYNTAX");
    }
    
    InMemoryTemplateInfo template = new InMemoryTemplateInfo(
        templateKey, templateContent, engineName, Optional.empty());
    templateRegistry.put(templateKey, template);
    
    logger.info("Template registered: {}, engine: {}", templateKey, engineName);
    return TemplateResult.success("Template registered successfully", template);
  }

  @Override
  public TemplateResult registerTemplateFromFile(String templateKey, Path templatePath) {
    return registerTemplateFromFile(templateKey, templatePath, defaultEngine);
  }

  @Override
  public TemplateResult registerTemplateFromFile(String templateKey, Path templatePath, String engineName) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to register template from file with null or empty key");
      return TemplateResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    if (templatePath == null) {
      logger.warn("Attempted to register template with null path: {}", templateKey);
      return TemplateResult.failure(
          "Template path cannot be null", "INVALID_PATH");
    }
    
    if (!SUPPORTED_ENGINES.contains(engineName)) {
      logger.warn("Attempted to register template from file with unsupported engine: {}, {}, {}", 
          templateKey, templatePath, engineName);
      return TemplateResult.failure(
          "Unsupported template engine: " + engineName, "UNSUPPORTED_ENGINE");
    }
    
    // Read the template content from the file
    String templateContent;
    try {
      templateContent = Files.readString(templatePath);
    } catch (IOException e) {
      logger.warn("Failed to read template file {}: {}", templatePath, e.getMessage());
      return TemplateResult.failure(
          "Failed to read template file: " + e.getMessage(), "IO_ERROR");
    }
    
    // Validate the template syntax based on the engine
    try {
      validateTemplateSyntax(templateContent, engineName);
    } catch (Exception e) {
      logger.warn("Template syntax validation failed for {}: {}", templateKey, e.getMessage());
      return TemplateResult.failure(
          "Template syntax validation failed: " + e.getMessage(), "INVALID_SYNTAX");
    }
    
    InMemoryTemplateInfo template = new InMemoryTemplateInfo(
        templateKey, templateContent, engineName, Optional.of(templatePath));
    templateRegistry.put(templateKey, template);
    
    logger.info("Template registered from file: {}, path: {}, engine: {}", 
        templateKey, templatePath, engineName);
    return TemplateResult.success("Template registered successfully from file", template);
  }

  @Override
  public TemplateRegistrationResult registerTemplatesFromDirectory(Path directory, String filePattern) {
    return registerTemplatesFromDirectory(directory, filePattern, defaultEngine);
  }

  @Override
  public TemplateRegistrationResult registerTemplatesFromDirectory(
      Path directory, String filePattern, String engineName) {
    if (directory == null) {
      logger.warn("Attempted to register templates with null directory");
      return TemplateRegistrationResult.failure(
          "Directory cannot be null", "INVALID_DIRECTORY");
    }
    
    if (filePattern == null || filePattern.trim().isEmpty()) {
      logger.warn("Attempted to register templates with null or empty file pattern");
      return TemplateRegistrationResult.failure(
          "File pattern cannot be null or empty", "INVALID_PATTERN");
    }
    
    if (!SUPPORTED_ENGINES.contains(engineName)) {
      logger.warn("Attempted to register templates with unsupported engine: {}, {}, {}", 
          directory, filePattern, engineName);
      return TemplateRegistrationResult.failure(
          "Unsupported template engine: " + engineName, "UNSUPPORTED_ENGINE");
    }
    
    if (!Files.isDirectory(directory)) {
      logger.warn("Attempted to register templates from non-existent directory: {}", directory);
      return TemplateRegistrationResult.failure(
          "Directory does not exist: " + directory, "DIRECTORY_NOT_FOUND");
    }
    
    // Find matching template files in the directory
    List<Path> templateFiles;
    try {
      templateFiles = Files.walk(directory, 1)
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().matches(filePattern))
          .collect(Collectors.toList());
    } catch (IOException e) {
      logger.warn("Failed to scan directory {}: {}", directory, e.getMessage());
      return TemplateRegistrationResult.failure(
          "Failed to scan directory: " + e.getMessage(), "IO_ERROR");
    }
    
    if (templateFiles.isEmpty()) {
      logger.warn("No matching template files found in {}", directory);
      return TemplateRegistrationResult.success(
          "No matching template files found", Map.of(), Map.of());
    }
    
    // Register each template file
    Map<String, TemplateInfo> successfulTemplates = new HashMap<>();
    Map<String, String> failedTemplates = new HashMap<>();
    
    for (Path templateFile : templateFiles) {
      String fileName = templateFile.getFileName().toString();
      String templateKey = fileName;
      
      // For files with extensions, use the file name without extension as the key
      int extensionIndex = fileName.lastIndexOf('.');
      if (extensionIndex > 0) {
        templateKey = fileName.substring(0, extensionIndex);
      }
      
      // Register the template
      TemplateResult result = registerTemplateFromFile(templateKey, templateFile, engineName);
      
      if (result.isSuccessful()) {
        successfulTemplates.put(templateKey, result.getTemplate().get());
      } else {
        failedTemplates.put(templateKey, result.getMessage());
      }
    }
    
    logger.info("Registered {} templates from directory {}, {} failed", 
        successfulTemplates.size(), directory, failedTemplates.size());
    
    return TemplateRegistrationResult.success(
        "Templates registered from directory", successfulTemplates, failedTemplates);
  }

  @Override
  public boolean hasTemplate(String templateKey) {
    return templateRegistry.containsKey(templateKey);
  }

  @Override
  public TemplateResult getTemplate(String templateKey) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to get template with null or empty key");
      return TemplateResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    TemplateInfo template = templateRegistry.get(templateKey);
    if (template == null) {
      logger.warn("Template not found: {}", templateKey);
      return TemplateResult.failure(
          "Template not found: " + templateKey, "TEMPLATE_NOT_FOUND");
    }
    
    logger.debug("Template retrieved: {}", templateKey);
    return TemplateResult.success("Template retrieved successfully", template);
  }

  @Override
  public OperationResult removeTemplate(String templateKey) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to remove template with null or empty key");
      return OperationResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    TemplateInfo removedTemplate = templateRegistry.remove(templateKey);
    if (removedTemplate == null) {
      logger.warn("Template not found for removal: {}", templateKey);
      return OperationResult.failure(
          "Template not found: " + templateKey, "TEMPLATE_NOT_FOUND");
    }
    
    logger.info("Template removed: {}", templateKey);
    return OperationResult.success("Template removed successfully");
  }

  @Override
  public Optional<String> getTemplateEngine(String templateKey) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to get engine for template with null or empty key");
      return Optional.empty();
    }
    
    TemplateInfo template = templateRegistry.get(templateKey);
    if (template == null) {
      logger.warn("Template not found for getting engine: {}", templateKey);
      return Optional.empty();
    }
    
    logger.debug("Template engine retrieved for {}: {}", templateKey, template.getEngine());
    return Optional.of(template.getEngine());
  }

  @Override
  public RenderResult renderTemplate(String templateKey, Map<String, Object> dataModel) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to render template with null or empty key");
      return RenderResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    TemplateInfo template = templateRegistry.get(templateKey);
    if (template == null) {
      logger.warn("Template not found for rendering: {}", templateKey);
      return RenderResult.failure(
          "Template not found: " + templateKey, "TEMPLATE_NOT_FOUND");
    }
    
    return renderString(template.getContent(), dataModel != null ? dataModel : Map.of(), template.getEngine());
  }

  @Override
  public RenderResult renderTemplate(String templateKey, Map<String, Object> dataModel, String engineName) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to render template with null or empty key");
      return RenderResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    if (!SUPPORTED_ENGINES.contains(engineName)) {
      logger.warn("Attempted to render template with unsupported engine: {}, {}", 
          templateKey, engineName);
      return RenderResult.failure(
          "Unsupported template engine: " + engineName, "UNSUPPORTED_ENGINE");
    }
    
    TemplateInfo template = templateRegistry.get(templateKey);
    if (template == null) {
      logger.warn("Template not found for rendering: {}", templateKey);
      return RenderResult.failure(
          "Template not found: " + templateKey, "TEMPLATE_NOT_FOUND");
    }
    
    return renderString(template.getContent(), dataModel != null ? dataModel : Map.of(), engineName);
  }

  @Override
  public RenderResult renderString(String templateContent, Map<String, Object> dataModel) {
    return renderString(templateContent, dataModel, defaultEngine);
  }

  @Override
  public RenderResult renderString(String templateContent, Map<String, Object> dataModel, String engineName) {
    if (templateContent == null) {
      logger.warn("Attempted to render null template content");
      return RenderResult.failure(
          "Template content cannot be null", "INVALID_CONTENT");
    }
    
    if (!SUPPORTED_ENGINES.contains(engineName)) {
      logger.warn("Attempted to render template string with unsupported engine: {}", engineName);
      return RenderResult.failure(
          "Unsupported template engine: " + engineName, "UNSUPPORTED_ENGINE");
    }
    
    // Render the template based on the engine
    try {
      String renderedContent = renderWithEngine(templateContent, dataModel, engineName);
      logger.debug("Template string rendered successfully with engine: {}", engineName);
      return RenderResult.success("Template rendered successfully", renderedContent);
    } catch (Exception e) {
      logger.warn("Failed to render template string with engine {}: {}", 
          engineName, e.getMessage());
      return RenderResult.failure(
          "Failed to render template: " + e.getMessage(), "RENDER_ERROR");
    }
  }

  @Override
  public DocumentResult generatePdf(String html, Path outputPath) {
    return generatePdf(html, outputPath, createDefaultPdfOptions());
  }

  @Override
  public DocumentResult generatePdf(String html, Path outputPath, PdfOptions options) {
    if (html == null) {
      logger.warn("Attempted to generate PDF with null HTML content");
      return DocumentResult.failure(
          "HTML content cannot be null", "INVALID_CONTENT");
    }
    
    if (outputPath == null) {
      logger.warn("Attempted to generate PDF with null output path");
      return DocumentResult.failure(
          "Output path cannot be null", "INVALID_PATH");
    }
    
    // Ensure parent directories exist
    try {
      Files.createDirectories(outputPath.getParent());
    } catch (IOException e) {
      logger.warn("Failed to create parent directories for {}: {}", outputPath, e.getMessage());
      return DocumentResult.failure(
          "Failed to create parent directories: " + e.getMessage(), "IO_ERROR");
    }
    
    // Generate PDF
    try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
      // In a real implementation, we would use a library like iText, Flying Saucer, etc.
      // For this mock implementation, we'll just write the HTML content to the file
      byte[] pdfContent = generatePdfFromHtml(html, options);
      fos.write(pdfContent);
      long size = pdfContent.length;
      
      logger.info("PDF generated successfully: {}, size: {} bytes", outputPath, size);
      return DocumentResult.successFile("PDF generated successfully", outputPath, size);
    } catch (Exception e) {
      logger.warn("Failed to generate PDF: {}", e.getMessage());
      return DocumentResult.failure(
          "Failed to generate PDF: " + e.getMessage(), "PDF_ERROR");
    }
  }

  @Override
  public DocumentResult generatePdf(String html, OutputStream outputStream) {
    return generatePdf(html, outputStream, createDefaultPdfOptions());
  }

  @Override
  public DocumentResult generatePdf(String html, OutputStream outputStream, PdfOptions options) {
    if (html == null) {
      logger.warn("Attempted to generate PDF with null HTML content");
      return DocumentResult.failure(
          "HTML content cannot be null", "INVALID_CONTENT");
    }
    
    if (outputStream == null) {
      logger.warn("Attempted to generate PDF with null output stream");
      return DocumentResult.failure(
          "Output stream cannot be null", "INVALID_STREAM");
    }
    
    // Generate PDF
    try {
      // In a real implementation, we would use a library like iText, Flying Saucer, etc.
      // For this mock implementation, we'll just write the HTML content to the stream
      byte[] pdfContent = generatePdfFromHtml(html, options);
      outputStream.write(pdfContent);
      long size = pdfContent.length;
      
      logger.info("PDF generated successfully to stream, size: {} bytes", size);
      return DocumentResult.successStream("PDF generated successfully", size);
    } catch (Exception e) {
      logger.warn("Failed to generate PDF to stream: {}", e.getMessage());
      return DocumentResult.failure(
          "Failed to generate PDF to stream: " + e.getMessage(), "PDF_ERROR");
    }
  }

  @Override
  public DocumentResult generateDocument(
      String templateKey, Map<String, Object> dataModel, OutputFormat outputFormat, Path outputPath) {
    return generateDocument(templateKey, dataModel, outputFormat, outputPath, createEmptyDocumentOptions());
  }

  @Override
  public DocumentResult generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      Path outputPath,
      DocumentOptions options) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to generate document with null or empty template key");
      return DocumentResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    if (outputFormat == null) {
      logger.warn("Attempted to generate document with null output format");
      return DocumentResult.failure(
          "Output format cannot be null", "INVALID_FORMAT");
    }
    
    if (outputPath == null) {
      logger.warn("Attempted to generate document with null output path");
      return DocumentResult.failure(
          "Output path cannot be null", "INVALID_PATH");
    }
    
    // First, render the template
    RenderResult renderResult = renderTemplate(templateKey, dataModel);
    if (!renderResult.isSuccessful()) {
      logger.warn("Failed to render template for document generation: {}", renderResult.getMessage());
      return DocumentResult.failure(
          "Failed to render template: " + renderResult.getMessage(), 
          renderResult.getErrorCode().orElse("RENDER_ERROR"));
    }
    
    String renderedContent = renderResult.getRenderedContent().get();
    
    // Ensure parent directories exist
    try {
      Files.createDirectories(outputPath.getParent());
    } catch (IOException e) {
      logger.warn("Failed to create parent directories for {}: {}", outputPath, e.getMessage());
      return DocumentResult.failure(
          "Failed to create parent directories: " + e.getMessage(), "IO_ERROR");
    }
    
    // Generate document based on the output format
    try {
      byte[] documentContent;
      
      switch (outputFormat) {
        case HTML:
          documentContent = renderedContent.getBytes(StandardCharsets.UTF_8);
          break;
        case PDF:
          documentContent = generatePdfFromHtml(renderedContent, 
              options instanceof PdfOptions ? (PdfOptions) options : createDefaultPdfOptions());
          break;
        case DOCX:
          documentContent = generateDocxFromHtml(renderedContent, options);
          break;
        case XLSX:
          documentContent = generateXlsxFromHtml(renderedContent, options);
          break;
        case TEXT:
          documentContent = convertHtmlToPlainText(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case MARKDOWN:
          documentContent = convertHtmlToMarkdown(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case RTF:
          documentContent = generateRtfFromHtml(renderedContent, options);
          break;
        case CSV:
          documentContent = convertHtmlToCsv(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case XML:
          documentContent = convertHtmlToXml(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case JSON:
          documentContent = convertHtmlToJson(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        default:
          logger.warn("Unsupported output format: {}", outputFormat);
          return DocumentResult.failure(
              "Unsupported output format: " + outputFormat, "UNSUPPORTED_FORMAT");
      }
      
      // Write the document content to the file
      Files.write(outputPath, documentContent);
      long size = documentContent.length;
      
      logger.info("{} document generated successfully: {}, size: {} bytes", 
          outputFormat, outputPath, size);
      return DocumentResult.successFile("Document generated successfully", outputPath, size);
    } catch (Exception e) {
      logger.warn("Failed to generate {} document: {}", outputFormat, e.getMessage());
      return DocumentResult.failure(
          "Failed to generate document: " + e.getMessage(), "DOCUMENT_ERROR");
    }
  }

  @Override
  public DocumentResult generateDocument(
      String templateKey, Map<String, Object> dataModel, OutputFormat outputFormat, OutputStream outputStream) {
    return generateDocument(
        templateKey, dataModel, outputFormat, outputStream, createEmptyDocumentOptions());
  }

  @Override
  public DocumentResult generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      OutputStream outputStream,
      DocumentOptions options) {
    if (templateKey == null || templateKey.trim().isEmpty()) {
      logger.warn("Attempted to generate document with null or empty template key");
      return DocumentResult.failure(
          "Template key cannot be null or empty", "INVALID_KEY");
    }
    
    if (outputFormat == null) {
      logger.warn("Attempted to generate document with null output format");
      return DocumentResult.failure(
          "Output format cannot be null", "INVALID_FORMAT");
    }
    
    if (outputStream == null) {
      logger.warn("Attempted to generate document with null output stream");
      return DocumentResult.failure(
          "Output stream cannot be null", "INVALID_STREAM");
    }
    
    // First, render the template
    RenderResult renderResult = renderTemplate(templateKey, dataModel);
    if (!renderResult.isSuccessful()) {
      logger.warn("Failed to render template for document generation: {}", renderResult.getMessage());
      return DocumentResult.failure(
          "Failed to render template: " + renderResult.getMessage(), 
          renderResult.getErrorCode().orElse("RENDER_ERROR"));
    }
    
    String renderedContent = renderResult.getRenderedContent().get();
    
    // Generate document based on the output format
    try {
      byte[] documentContent;
      
      switch (outputFormat) {
        case HTML:
          documentContent = renderedContent.getBytes(StandardCharsets.UTF_8);
          break;
        case PDF:
          documentContent = generatePdfFromHtml(renderedContent, 
              options instanceof PdfOptions ? (PdfOptions) options : createDefaultPdfOptions());
          break;
        case DOCX:
          documentContent = generateDocxFromHtml(renderedContent, options);
          break;
        case XLSX:
          documentContent = generateXlsxFromHtml(renderedContent, options);
          break;
        case TEXT:
          documentContent = convertHtmlToPlainText(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case MARKDOWN:
          documentContent = convertHtmlToMarkdown(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case RTF:
          documentContent = generateRtfFromHtml(renderedContent, options);
          break;
        case CSV:
          documentContent = convertHtmlToCsv(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case XML:
          documentContent = convertHtmlToXml(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        case JSON:
          documentContent = convertHtmlToJson(renderedContent).getBytes(StandardCharsets.UTF_8);
          break;
        default:
          logger.warn("Unsupported output format: {}", outputFormat);
          return DocumentResult.failure(
              "Unsupported output format: " + outputFormat, "UNSUPPORTED_FORMAT");
      }
      
      // Write the document content to the stream
      outputStream.write(documentContent);
      long size = documentContent.length;
      
      logger.info("{} document generated successfully to stream, size: {} bytes", 
          outputFormat, size);
      return DocumentResult.successStream("Document generated successfully", size);
    } catch (Exception e) {
      logger.warn("Failed to generate {} document to stream: {}", outputFormat, e.getMessage());
      return DocumentResult.failure(
          "Failed to generate document to stream: " + e.getMessage(), "DOCUMENT_ERROR");
    }
  }

  /**
   * Validates the syntax of a template based on the template engine.
   *
   * @param templateContent The template content to validate
   * @param engineName The template engine name
   * @throws Exception If the template syntax is invalid
   */
  private void validateTemplateSyntax(String templateContent, String engineName) throws Exception {
    // In a real implementation, we would use the actual template engine to validate the syntax
    // For this mock implementation, we'll just do some basic validation
    
    switch (engineName) {
      case FREEMARKER_ENGINE:
        // Check for basic Freemarker syntax
        if (templateContent.contains("${") && !templateContent.contains("}")) {
          throw new Exception("Unclosed variable in Freemarker template");
        }
        break;
      case VELOCITY_ENGINE:
        // Check for basic Velocity syntax
        if (templateContent.contains("${") && !templateContent.contains("}")) {
          throw new Exception("Unclosed variable in Velocity template");
        }
        break;
      case THYMELEAF_ENGINE:
        // Check for basic Thymeleaf syntax
        if (templateContent.contains("th:") && !templateContent.contains("xmlns:th")) {
          throw new Exception("Missing Thymeleaf namespace declaration");
        }
        break;
      default:
        throw new Exception("Unsupported template engine: " + engineName);
    }
  }

  /**
   * Renders a template with the specified engine.
   *
   * @param templateContent The template content to render
   * @param dataModel The data model to use for rendering
   * @param engineName The template engine name
   * @return The rendered content
   * @throws Exception If rendering fails
   */
  private String renderWithEngine(String templateContent, Map<String, Object> dataModel, String engineName) 
      throws Exception {
    // In a real implementation, we would use the actual template engine to render the template
    // For this mock implementation, we'll just do a simple string replacement
    
    String renderedContent = templateContent;
    
    switch (engineName) {
      case FREEMARKER_ENGINE:
        // Simple replacement for Freemarker-like variables
        for (Map.Entry<String, Object> entry : dataModel.entrySet()) {
          String placeholder = "${" + entry.getKey() + "}";
          String value = entry.getValue() != null ? entry.getValue().toString() : "";
          renderedContent = renderedContent.replace(placeholder, value);
        }
        break;
      case VELOCITY_ENGINE:
        // Simple replacement for Velocity-like variables
        for (Map.Entry<String, Object> entry : dataModel.entrySet()) {
          String placeholder = "${" + entry.getKey() + "}";
          String value = entry.getValue() != null ? entry.getValue().toString() : "";
          renderedContent = renderedContent.replace(placeholder, value);
          
          placeholder = "$" + entry.getKey();
          renderedContent = renderedContent.replace(placeholder, value);
        }
        break;
      case THYMELEAF_ENGINE:
        // Simple replacement for Thymeleaf-like expressions
        for (Map.Entry<String, Object> entry : dataModel.entrySet()) {
          String placeholder = "th:text=\"${" + entry.getKey() + "}\"";
          String value = entry.getValue() != null ? entry.getValue().toString() : "";
          renderedContent = renderedContent.replace(placeholder, ">" + value + "<");
        }
        break;
      default:
        throw new Exception("Unsupported template engine: " + engineName);
    }
    
    return renderedContent;
  }

  /**
   * Generates a PDF document from HTML content.
   *
   * @param html The HTML content
   * @param options The PDF generation options
   * @return The PDF document as a byte array
   * @throws Exception If PDF generation fails
   */
  private byte[] generatePdfFromHtml(String html, PdfOptions options) throws Exception {
    // In a real implementation, we would use a library like iText, Flying Saucer, etc.
    // For this mock implementation, we'll just return the HTML content with a PDF header
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write("%PDF-1.7\n".getBytes(StandardCharsets.UTF_8));
    baos.write(html.getBytes(StandardCharsets.UTF_8));
    return baos.toByteArray();
  }

  /**
   * Generates a DOCX document from HTML content.
   *
   * @param html The HTML content
   * @param options The document generation options
   * @return The DOCX document as a byte array
   * @throws Exception If DOCX generation fails
   */
  private byte[] generateDocxFromHtml(String html, DocumentOptions options) throws Exception {
    // In a real implementation, we would use a library like Apache POI, docx4j, etc.
    // For this mock implementation, we'll just return the HTML content with a DOCX header
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write("PK\003\004".getBytes(StandardCharsets.UTF_8)); // DOCX header
    baos.write(html.getBytes(StandardCharsets.UTF_8));
    return baos.toByteArray();
  }

  /**
   * Generates an XLSX document from HTML content.
   *
   * @param html The HTML content
   * @param options The document generation options
   * @return The XLSX document as a byte array
   * @throws Exception If XLSX generation fails
   */
  private byte[] generateXlsxFromHtml(String html, DocumentOptions options) throws Exception {
    // In a real implementation, we would use a library like Apache POI, etc.
    // For this mock implementation, we'll just return the HTML content with an XLSX header
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write("PK\003\004".getBytes(StandardCharsets.UTF_8)); // XLSX header
    baos.write(html.getBytes(StandardCharsets.UTF_8));
    return baos.toByteArray();
  }

  /**
   * Generates an RTF document from HTML content.
   *
   * @param html The HTML content
   * @param options The document generation options
   * @return The RTF document as a byte array
   * @throws Exception If RTF generation fails
   */
  private byte[] generateRtfFromHtml(String html, DocumentOptions options) throws Exception {
    // In a real implementation, we would use a library for HTML to RTF conversion
    // For this mock implementation, we'll just return the HTML content with an RTF header
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write("{\\rtf1\\ansi\\ansicpg1252\\cocoartf1671\\cocoasubrtf600\n".getBytes(StandardCharsets.UTF_8));
    baos.write(html.getBytes(StandardCharsets.UTF_8));
    baos.write("}".getBytes(StandardCharsets.UTF_8));
    return baos.toByteArray();
  }

  /**
   * Converts HTML content to plain text.
   *
   * @param html The HTML content
   * @return The plain text content
   */
  private String convertHtmlToPlainText(String html) {
    // In a real implementation, we would use a library like jsoup, etc.
    // For this mock implementation, we'll just strip basic HTML tags
    return html.replaceAll("<[^>]*>", "");
  }

  /**
   * Converts HTML content to Markdown.
   *
   * @param html The HTML content
   * @return The Markdown content
   */
  private String convertHtmlToMarkdown(String html) {
    // In a real implementation, we would use a library like flexmark, etc.
    // For this mock implementation, we'll just do some basic conversions
    String markdown = html;
    markdown = markdown.replaceAll("<h1[^>]*>(.*?)</h1>", "# $1\n\n");
    markdown = markdown.replaceAll("<h2[^>]*>(.*?)</h2>", "## $1\n\n");
    markdown = markdown.replaceAll("<h3[^>]*>(.*?)</h3>", "### $1\n\n");
    markdown = markdown.replaceAll("<p[^>]*>(.*?)</p>", "$1\n\n");
    markdown = markdown.replaceAll("<strong[^>]*>(.*?)</strong>", "**$1**");
    markdown = markdown.replaceAll("<em[^>]*>(.*?)</em>", "*$1*");
    markdown = markdown.replaceAll("<a[^>]*href=\"(.*?)\"[^>]*>(.*?)</a>", "[$2]($1)");
    markdown = markdown.replaceAll("<ul[^>]*>(.*?)</ul>", "$1");
    markdown = markdown.replaceAll("<li[^>]*>(.*?)</li>", "* $1\n");
    return markdown;
  }

  /**
   * Converts HTML content to CSV.
   *
   * @param html The HTML content
   * @return The CSV content
   */
  private String convertHtmlToCsv(String html) {
    // In a real implementation, we would use a library like jsoup, etc.
    // For this mock implementation, we'll just extract table data if present
    StringBuilder csv = new StringBuilder();
    
    // Very basic table extraction - won't work for complex tables
    int tableStart = html.indexOf("<table");
    int tableEnd = html.indexOf("</table>");
    
    if (tableStart >= 0 && tableEnd > tableStart) {
      String table = html.substring(tableStart, tableEnd + 8);
      String[] rows = table.split("<tr[^>]*>");
      
      for (int i = 1; i < rows.length; i++) { // Skip the first split which is before the first <tr>
        String row = rows[i];
        String[] cells;
        
        if (i == 1 && row.contains("<th")) {
          cells = row.split("<th[^>]*>");
        } else {
          cells = row.split("<td[^>]*>");
        }
        
        List<String> cellValues = new ArrayList<>();
        
        for (int j = 1; j < cells.length; j++) { // Skip the first split which is before the first <th> or <td>
          String cell = cells[j];
          int cellEnd = cell.indexOf("</td>");
          if (cellEnd < 0) {
            cellEnd = cell.indexOf("</th>");
          }
          
          if (cellEnd >= 0) {
            String cellValue = cell.substring(0, cellEnd).trim();
            // Escape quotes in CSV
            cellValue = cellValue.replace("\"", "\"\"");
            cellValues.add("\"" + cellValue + "\"");
          }
        }
        
        csv.append(String.join(",", cellValues)).append("\n");
      }
    } else {
      // If no table found, just return text content
      csv.append(convertHtmlToPlainText(html));
    }
    
    return csv.toString();
  }

  /**
   * Converts HTML content to XML.
   *
   * @param html The HTML content
   * @return The XML content
   */
  private String convertHtmlToXml(String html) {
    // In a real implementation, we would use a library like jsoup, etc.
    // For this mock implementation, we'll just wrap the HTML in an XML document
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document>\n" + html + "\n</document>";
  }

  /**
   * Converts HTML content to JSON.
   *
   * @param html The HTML content
   * @return The JSON content
   */
  private String convertHtmlToJson(String html) {
    // In a real implementation, we would use a library like jsoup, etc.
    // For this mock implementation, we'll just wrap the HTML content in a JSON object
    String escaped = html.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    return "{\n  \"html\": \"" + escaped + "\"\n}";
  }

  /**
   * Creates default PDF options.
   *
   * @return The default PDF options
   */
  private PdfOptions createDefaultPdfOptions() {
    return new PdfOptions() {
      @Override
      public PageSize getPageSize() {
        return PageSize.A4;
      }

      @Override
      public PageOrientation getOrientation() {
        return PageOrientation.PORTRAIT;
      }

      @Override
      public Margins getMargins() {
        return new Margins() {
          @Override
          public float getTop() {
            return 72; // 1 inch
          }

          @Override
          public float getRight() {
            return 72;
          }

          @Override
          public float getBottom() {
            return 72;
          }

          @Override
          public float getLeft() {
            return 72;
          }
        };
      }

      @Override
      public boolean isCompressed() {
        return true;
      }

      @Override
      public boolean isPdfA() {
        return false;
      }

      @Override
      public boolean includeTableOfContents() {
        return false;
      }

      @Override
      public Optional<String> getBaseUrl() {
        return Optional.empty();
      }

      @Override
      public Map<String, String> getMetadata() {
        return Map.of();
      }
    };
  }

  /**
   * Creates empty document options.
   *
   * @return The empty document options
   */
  private DocumentOptions createEmptyDocumentOptions() {
    return () -> Map.of();
  }

  /**
   * In-memory implementation of the TemplateInfo interface.
   */
  private static class InMemoryTemplateInfo implements TemplateInfo {
    private final String key;
    private final String content;
    private final String engine;
    private final Optional<Path> sourcePath;

    /**
     * Constructs an InMemoryTemplateInfo.
     *
     * @param key The template key
     * @param content The template content
     * @param engine The template engine
     * @param sourcePath The source path, if loaded from a file
     */
    public InMemoryTemplateInfo(String key, String content, String engine, Optional<Path> sourcePath) {
      this.key = key;
      this.content = content;
      this.engine = engine;
      this.sourcePath = sourcePath;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public String getContent() {
      return content;
    }

    @Override
    public String getEngine() {
      return engine;
    }

    @Override
    public Optional<Path> getSourcePath() {
      return sourcePath;
    }
  }
}