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

package org.s8r.application.port.template;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Port interface for template rendering and document generation operations.
 *
 * <p>This interface defines the contract for template processing and document generation in the
 * application. Following the ports and adapters pattern, this is an output port in the application
 * layer, which will be implemented by adapters in the infrastructure layer.
 *
 * <p>The TemplatePort provides operations for compiling templates, rendering templates with data
 * models, and generating documents in various formats.
 */
public interface TemplatePort {

  /**
   * Gets all registered template engines.
   *
   * @return A set of registered template engine names
   */
  Set<String> getRegisteredEngines();

  /**
   * Gets the default template engine.
   *
   * @return The default template engine name
   */
  String getDefaultEngine();

  /**
   * Sets the default template engine.
   *
   * @param engineName The template engine name to set as default
   * @return The result of the operation
   */
  OperationResult setDefaultEngine(String engineName);

  /**
   * Registers a template in the template registry.
   *
   * @param templateKey The template key/name
   * @param templateContent The template content
   * @return The result of the operation
   */
  TemplateResult registerTemplate(String templateKey, String templateContent);

  /**
   * Registers a template in the template registry with a specific engine.
   *
   * @param templateKey The template key/name
   * @param templateContent The template content
   * @param engineName The engine to use for this template
   * @return The result of the operation
   */
  TemplateResult registerTemplate(String templateKey, String templateContent, String engineName);

  /**
   * Loads a template from a file and registers it in the template registry.
   *
   * @param templateKey The template key/name
   * @param templatePath The path to the template file
   * @return The result of the operation
   */
  TemplateResult registerTemplateFromFile(String templateKey, Path templatePath);

  /**
   * Loads a template from a file and registers it in the template registry with a specific engine.
   *
   * @param templateKey The template key/name
   * @param templatePath The path to the template file
   * @param engineName The engine to use for this template
   * @return The result of the operation
   */
  TemplateResult registerTemplateFromFile(String templateKey, Path templatePath, String engineName);

  /**
   * Loads and registers all templates from a directory.
   *
   * @param directory The directory containing templates
   * @param filePattern The pattern to match template files (e.g., "*.ftl", "*.vm")
   * @return The result of the operation
   */
  TemplateRegistrationResult registerTemplatesFromDirectory(Path directory, String filePattern);

  /**
   * Loads and registers all templates from a directory with a specific engine.
   *
   * @param directory The directory containing templates
   * @param filePattern The pattern to match template files (e.g., "*.ftl", "*.vm")
   * @param engineName The engine to use for these templates
   * @return The result of the operation
   */
  TemplateRegistrationResult registerTemplatesFromDirectory(
      Path directory, String filePattern, String engineName);

  /**
   * Checks if a template is registered.
   *
   * @param templateKey The template key/name
   * @return True if the template is registered, false otherwise
   */
  boolean hasTemplate(String templateKey);

  /**
   * Gets a registered template.
   *
   * @param templateKey The template key/name
   * @return The result of the operation
   */
  TemplateResult getTemplate(String templateKey);

  /**
   * Removes a template from the registry.
   *
   * @param templateKey The template key/name
   * @return The result of the operation
   */
  OperationResult removeTemplate(String templateKey);

  /**
   * Gets the engine for a registered template.
   *
   * @param templateKey The template key/name
   * @return The engine name if the template is registered, empty otherwise
   */
  Optional<String> getTemplateEngine(String templateKey);

  /**
   * Renders a template with the provided data model.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @return The result of the operation
   */
  RenderResult renderTemplate(String templateKey, Map<String, Object> dataModel);

  /**
   * Renders a template with the provided data model using a specific engine.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param engineName The engine to use for rendering
   * @return The result of the operation
   */
  RenderResult renderTemplate(String templateKey, Map<String, Object> dataModel, String engineName);

  /**
   * Renders a template string directly with the provided data model.
   *
   * @param templateContent The template content
   * @param dataModel The data model
   * @return The result of the operation
   */
  RenderResult renderString(String templateContent, Map<String, Object> dataModel);

  /**
   * Renders a template string directly with the provided data model using a specific engine.
   *
   * @param templateContent The template content
   * @param dataModel The data model
   * @param engineName The engine to use for rendering
   * @return The result of the operation
   */
  RenderResult renderString(
      String templateContent, Map<String, Object> dataModel, String engineName);

  /**
   * Generates a PDF document from a rendered HTML template.
   *
   * @param html The HTML content
   * @param outputPath The path where to save the PDF
   * @return The result of the operation
   */
  DocumentResult generatePdf(String html, Path outputPath);

  /**
   * Generates a PDF document from a rendered HTML template with options.
   *
   * @param html The HTML content
   * @param outputPath The path where to save the PDF
   * @param options The PDF generation options
   * @return The result of the operation
   */
  DocumentResult generatePdf(String html, Path outputPath, PdfOptions options);

  /**
   * Generates a PDF document from a rendered HTML template and writes it to an output stream.
   *
   * @param html The HTML content
   * @param outputStream The output stream to write the PDF to
   * @return The result of the operation
   */
  DocumentResult generatePdf(String html, OutputStream outputStream);

  /**
   * Generates a PDF document from a rendered HTML template with options and writes it to an output
   * stream.
   *
   * @param html The HTML content
   * @param outputStream The output stream to write the PDF to
   * @param options The PDF generation options
   * @return The result of the operation
   */
  DocumentResult generatePdf(String html, OutputStream outputStream, PdfOptions options);

  /**
   * Generates a document from a template and data model.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputPath The path where to save the document
   * @return The result of the operation
   */
  DocumentResult generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      Path outputPath);

  /**
   * Generates a document from a template and data model with options.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputPath The path where to save the document
   * @param options The document generation options
   * @return The result of the operation
   */
  DocumentResult generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      Path outputPath,
      DocumentOptions options);

  /**
   * Generates a document from a template and data model and writes it to an output stream.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputStream The output stream to write the document to
   * @return The result of the operation
   */
  DocumentResult generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      OutputStream outputStream);

  /**
   * Generates a document from a template and data model with options and writes it to an output
   * stream.
   *
   * @param templateKey The template key/name
   * @param dataModel The data model
   * @param outputFormat The output format
   * @param outputStream The output stream to write the document to
   * @param options The document generation options
   * @return The result of the operation
   */
  DocumentResult generateDocument(
      String templateKey,
      Map<String, Object> dataModel,
      OutputFormat outputFormat,
      OutputStream outputStream,
      DocumentOptions options);

  // Nested interfaces and classes

  /** Output formats for document generation. */
  enum OutputFormat {
    /** HTML format */
    HTML,
    /** PDF format */
    PDF,
    /** Microsoft Word format */
    DOCX,
    /** Microsoft Excel format */
    XLSX,
    /** Plain text format */
    TEXT,
    /** Markdown format */
    MARKDOWN,
    /** Rich Text Format */
    RTF,
    /** CSV format */
    CSV,
    /** XML format */
    XML,
    /** JSON format */
    JSON
  }

  /** Template information. */
  interface TemplateInfo {
    /**
     * Gets the template key/name.
     *
     * @return The template key/name
     */
    String getKey();

    /**
     * Gets the template content.
     *
     * @return The template content
     */
    String getContent();

    /**
     * Gets the engine for this template.
     *
     * @return The engine name
     */
    String getEngine();

    /**
     * Gets the source path if the template was loaded from a file.
     *
     * @return The source path, if available
     */
    Optional<Path> getSourcePath();
  }

  /** PDF generation options. */
  interface PdfOptions extends DocumentOptions {
    /**
     * Gets the page size.
     *
     * @return The page size
     */
    PageSize getPageSize();

    /**
     * Gets the page orientation.
     *
     * @return The page orientation
     */
    PageOrientation getOrientation();

    /**
     * Gets the page margins in points.
     *
     * @return The page margins
     */
    Margins getMargins();

    /**
     * Gets whether to compress the PDF.
     *
     * @return True if compression is enabled, false otherwise
     */
    boolean isCompressed();

    /**
     * Gets whether to enable PDF/A compliance.
     *
     * @return True if PDF/A compliance is enabled, false otherwise
     */
    boolean isPdfA();

    /**
     * Gets whether to include table of contents.
     *
     * @return True if table of contents is enabled, false otherwise
     */
    boolean includeTableOfContents();

    /**
     * Gets the base URL for resolving relative URLs in the HTML.
     *
     * @return The base URL, if set
     */
    Optional<String> getBaseUrl();

    /** Builder for PDF options. */
    interface Builder {
      /**
       * Sets the page size.
       *
       * @param pageSize The page size
       * @return This builder
       */
      Builder pageSize(PageSize pageSize);

      /**
       * Sets the page orientation.
       *
       * @param orientation The page orientation
       * @return This builder
       */
      Builder orientation(PageOrientation orientation);

      /**
       * Sets the page margins.
       *
       * @param margins The page margins
       * @return This builder
       */
      Builder margins(Margins margins);

      /**
       * Sets whether to compress the PDF.
       *
       * @param compressed True to enable compression, false otherwise
       * @return This builder
       */
      Builder compressed(boolean compressed);

      /**
       * Sets whether to enable PDF/A compliance.
       *
       * @param pdfA True to enable PDF/A compliance, false otherwise
       * @return This builder
       */
      Builder pdfA(boolean pdfA);

      /**
       * Sets whether to include table of contents.
       *
       * @param includeTableOfContents True to include table of contents, false otherwise
       * @return This builder
       */
      Builder includeTableOfContents(boolean includeTableOfContents);

      /**
       * Sets the base URL for resolving relative URLs in the HTML.
       *
       * @param baseUrl The base URL
       * @return This builder
       */
      Builder baseUrl(String baseUrl);

      /**
       * Sets document metadata.
       *
       * @param metadata The document metadata
       * @return This builder
       */
      Builder metadata(Map<String, String> metadata);

      /**
       * Builds the PdfOptions.
       *
       * @return The built PdfOptions
       */
      PdfOptions build();
    }
  }

  /** Common options for document generation. */
  interface DocumentOptions {
    /**
     * Gets the document metadata.
     *
     * @return The document metadata
     */
    Map<String, String> getMetadata();
  }

  /** Page sizes for PDF generation. */
  enum PageSize {
    /** A4 paper size */
    A4,
    /** A3 paper size */
    A3,
    /** Letter paper size */
    LETTER,
    /** Legal paper size */
    LEGAL,
    /** Tabloid paper size */
    TABLOID
  }

  /** Page orientations for PDF generation. */
  enum PageOrientation {
    /** Portrait orientation */
    PORTRAIT,
    /** Landscape orientation */
    LANDSCAPE
  }

  /** Page margins for PDF generation. */
  interface Margins {
    /**
     * Gets the top margin in points.
     *
     * @return The top margin
     */
    float getTop();

    /**
     * Gets the right margin in points.
     *
     * @return The right margin
     */
    float getRight();

    /**
     * Gets the bottom margin in points.
     *
     * @return The bottom margin
     */
    float getBottom();

    /**
     * Gets the left margin in points.
     *
     * @return The left margin
     */
    float getLeft();
  }

  /** Base class for operation results. */
  abstract class OperationResult {
    private final boolean successful;
    private final String message;
    private final Optional<String> errorCode;

    /**
     * Constructs a successful OperationResult.
     *
     * @param message The success message
     */
    protected OperationResult(String message) {
      this.successful = true;
      this.message = message;
      this.errorCode = Optional.empty();
    }

    /**
     * Constructs a failed OperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     */
    protected OperationResult(String message, String errorCode) {
      this.successful = false;
      this.message = message;
      this.errorCode = Optional.of(errorCode);
    }

    /**
     * Gets whether the operation was successful.
     *
     * @return True if successful, false otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Gets the result message.
     *
     * @return The message
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the error code, if any.
     *
     * @return The error code, if available
     */
    public Optional<String> getErrorCode() {
      return errorCode;
    }

    /**
     * Creates a successful OperationResult.
     *
     * @param message The success message
     * @return The OperationResult
     */
    public static OperationResult success(String message) {
      return new OperationResult(message) {};
    }

    /**
     * Creates a failed OperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The OperationResult
     */
    public static OperationResult failure(String message, String errorCode) {
      return new OperationResult(message, errorCode) {};
    }
  }

  /** Result of a template operation. */
  class TemplateResult extends OperationResult {
    private final Optional<TemplateInfo> template;

    private TemplateResult(String message, TemplateInfo template) {
      super(message);
      this.template = Optional.of(template);
    }

    private TemplateResult(String message, String errorCode) {
      super(message, errorCode);
      this.template = Optional.empty();
    }

    /**
     * Gets the template, if the operation was successful.
     *
     * @return The template, if available
     */
    public Optional<TemplateInfo> getTemplate() {
      return template;
    }

    /**
     * Creates a successful TemplateResult.
     *
     * @param message The success message
     * @param template The template
     * @return The TemplateResult
     */
    public static TemplateResult success(String message, TemplateInfo template) {
      return new TemplateResult(message, template);
    }

    /**
     * Creates a failed TemplateResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The TemplateResult
     */
    public static TemplateResult failure(String message, String errorCode) {
      return new TemplateResult(message, errorCode);
    }
  }

  /** Result of a template registration operation that registers multiple templates. */
  class TemplateRegistrationResult extends OperationResult {
    private final Map<String, TemplateInfo> templates;
    private final Map<String, String> failedTemplates;

    private TemplateRegistrationResult(
        String message, Map<String, TemplateInfo> templates, Map<String, String> failedTemplates) {
      super(message);
      this.templates = templates;
      this.failedTemplates = failedTemplates;
    }

    private TemplateRegistrationResult(String message, String errorCode) {
      super(message, errorCode);
      this.templates = Map.of();
      this.failedTemplates = Map.of();
    }

    /**
     * Gets the successfully registered templates.
     *
     * @return The templates
     */
    public Map<String, TemplateInfo> getTemplates() {
      return templates;
    }

    /**
     * Gets the templates that failed to register and their error messages.
     *
     * @return The failed templates
     */
    public Map<String, String> getFailedTemplates() {
      return failedTemplates;
    }

    /**
     * Gets the total number of processed templates.
     *
     * @return The total number of processed templates
     */
    public int getTotalTemplates() {
      return templates.size() + failedTemplates.size();
    }

    /**
     * Gets the number of successfully registered templates.
     *
     * @return The number of successfully registered templates
     */
    public int getSuccessfulCount() {
      return templates.size();
    }

    /**
     * Gets the number of templates that failed to register.
     *
     * @return The number of templates that failed to register
     */
    public int getFailedCount() {
      return failedTemplates.size();
    }

    /**
     * Creates a successful TemplateRegistrationResult.
     *
     * @param message The success message
     * @param templates The successfully registered templates
     * @param failedTemplates The templates that failed to register and their error messages
     * @return The TemplateRegistrationResult
     */
    public static TemplateRegistrationResult success(
        String message, Map<String, TemplateInfo> templates, Map<String, String> failedTemplates) {
      return new TemplateRegistrationResult(message, templates, failedTemplates);
    }

    /**
     * Creates a failed TemplateRegistrationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The TemplateRegistrationResult
     */
    public static TemplateRegistrationResult failure(String message, String errorCode) {
      return new TemplateRegistrationResult(message, errorCode);
    }
  }

  /** Result of a template rendering operation. */
  class RenderResult extends OperationResult {
    private final Optional<String> renderedContent;

    private RenderResult(String message, String renderedContent) {
      super(message);
      this.renderedContent = Optional.of(renderedContent);
    }

    private RenderResult(String message, String errorCode, boolean isError) {
      super(message, errorCode);
      this.renderedContent = Optional.empty();
    }

    /**
     * Gets the rendered content, if the operation was successful.
     *
     * @return The rendered content, if available
     */
    public Optional<String> getRenderedContent() {
      return renderedContent;
    }

    /**
     * Creates a successful RenderResult.
     *
     * @param message The success message
     * @param renderedContent The rendered content
     * @return The RenderResult
     */
    public static RenderResult success(String message, String renderedContent) {
      return new RenderResult(message, renderedContent);
    }

    /**
     * Creates a failed RenderResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The RenderResult
     */
    public static RenderResult failure(String message, String errorCode) {
      return new RenderResult(message, errorCode, true);
    }
  }

  /** Result of a document generation operation. */
  class DocumentResult extends OperationResult {
    private final Optional<Path> outputPath;
    private final boolean wroteToStream;
    private final Optional<Long> documentSize;

    private DocumentResult(String message, Path outputPath, long documentSize) {
      super(message);
      this.outputPath = Optional.of(outputPath);
      this.wroteToStream = false;
      this.documentSize = Optional.of(documentSize);
    }

    private DocumentResult(String message, long documentSize) {
      super(message);
      this.outputPath = Optional.empty();
      this.wroteToStream = true;
      this.documentSize = Optional.of(documentSize);
    }

    private DocumentResult(String message, String errorCode) {
      super(message, errorCode);
      this.outputPath = Optional.empty();
      this.wroteToStream = false;
      this.documentSize = Optional.empty();
    }

    /**
     * Gets the output path, if the operation was successful and wrote to a file.
     *
     * @return The output path, if available
     */
    public Optional<Path> getOutputPath() {
      return outputPath;
    }

    /**
     * Gets whether the operation wrote to a stream.
     *
     * @return True if the operation wrote to a stream, false otherwise
     */
    public boolean wroteToStream() {
      return wroteToStream;
    }

    /**
     * Gets the document size in bytes, if the operation was successful.
     *
     * @return The document size, if available
     */
    public Optional<Long> getDocumentSize() {
      return documentSize;
    }

    /**
     * Creates a successful DocumentResult for a file output.
     *
     * @param message The success message
     * @param outputPath The output path
     * @param documentSize The document size in bytes
     * @return The DocumentResult
     */
    public static DocumentResult successFile(String message, Path outputPath, long documentSize) {
      return new DocumentResult(message, outputPath, documentSize);
    }

    /**
     * Creates a successful DocumentResult for a stream output.
     *
     * @param message The success message
     * @param documentSize The document size in bytes
     * @return The DocumentResult
     */
    public static DocumentResult successStream(String message, long documentSize) {
      return new DocumentResult(message, documentSize);
    }

    /**
     * Creates a failed DocumentResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The DocumentResult
     */
    public static DocumentResult failure(String message, String errorCode) {
      return new DocumentResult(message, errorCode);
    }
  }
}
