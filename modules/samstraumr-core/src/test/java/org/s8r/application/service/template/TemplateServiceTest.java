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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.template.TemplatePort;
import org.s8r.application.port.template.TemplatePort.*;

class TemplateServiceTest {

  @Mock private TemplatePort templatePort;

  @Mock private LoggerPort logger;

  private TemplateService service;

  @TempDir Path tempDir;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new TemplateService(templatePort, logger);
  }

  @Test
  void getRegisteredEngines_shouldReturnEngines() {
    // Arrange
    Set<String> engines = Set.of("freemarker", "velocity", "thymeleaf");
    when(templatePort.getRegisteredEngines()).thenReturn(engines);

    // Act
    Set<String> result = service.getRegisteredEngines();

    // Assert
    assertEquals(engines, result);
    verify(logger).debug(contains("Getting registered template engines"));
    verify(logger).debug(contains("Found"), eq(engines.size()), anyString());
  }

  @Test
  void getDefaultEngine_shouldReturnEngine() {
    // Arrange
    String engine = "freemarker";
    when(templatePort.getDefaultEngine()).thenReturn(engine);

    // Act
    String result = service.getDefaultEngine();

    // Assert
    assertEquals(engine, result);
    verify(logger).debug(contains("Getting default template engine"));
    verify(logger).debug(contains("Default template engine"), eq(engine));
  }

  @Test
  void setDefaultEngine_shouldReturnTrue_whenOperationSucceeds() {
    // Arrange
    String engine = "velocity";
    when(templatePort.setDefaultEngine(engine))
        .thenReturn(OperationResult.success("Default engine set successfully"));

    // Act
    boolean result = service.setDefaultEngine(engine);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Setting default template engine"), eq(engine));
    verify(logger).debug(contains("Default template engine set to"), eq(engine));
  }

  @Test
  void setDefaultEngine_shouldReturnFalse_whenOperationFails() {
    // Arrange
    String engine = "unknown";
    when(templatePort.setDefaultEngine(engine))
        .thenReturn(OperationResult.failure("Unsupported engine", "UNSUPPORTED_ENGINE"));

    // Act
    boolean result = service.setDefaultEngine(engine);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Setting default template engine"), eq(engine));
    verify(logger).warn(contains("Failed to set default template engine"), eq(engine), anyString());
  }

  @Test
  void registerTemplate_shouldReturnTrue_whenRegistrationSucceeds() {
    // Arrange
    String templateKey = "test-template";
    String templateContent = "Hello, ${name}!";
    TemplateInfo mockTemplate = mock(TemplateInfo.class);

    when(templatePort.registerTemplate(templateKey, templateContent))
        .thenReturn(TemplateResult.success("Template registered successfully", mockTemplate));

    // Act
    boolean result = service.registerTemplate(templateKey, templateContent);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Registering template"), eq(templateKey));
    verify(logger).debug(contains("Template registered successfully"), eq(templateKey));
  }

  @Test
  void registerTemplate_shouldReturnFalse_whenRegistrationFails() {
    // Arrange
    String templateKey = "test-template";
    String templateContent = "Hello, ${name!";

    when(templatePort.registerTemplate(templateKey, templateContent))
        .thenReturn(TemplateResult.failure("Invalid template syntax", "INVALID_SYNTAX"));

    // Act
    boolean result = service.registerTemplate(templateKey, templateContent);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Registering template"), eq(templateKey));
    verify(logger).warn(contains("Failed to register template"), eq(templateKey), anyString());
  }

  @Test
  void registerTemplate_withEngine_shouldReturnTrue_whenRegistrationSucceeds() {
    // Arrange
    String templateKey = "test-template";
    String templateContent = "Hello, ${name}!";
    String engine = "velocity";
    TemplateInfo mockTemplate = mock(TemplateInfo.class);

    when(templatePort.registerTemplate(templateKey, templateContent, engine))
        .thenReturn(TemplateResult.success("Template registered successfully", mockTemplate));

    // Act
    boolean result = service.registerTemplate(templateKey, templateContent, engine);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Registering template with engine"), eq(templateKey), eq(engine));
    verify(logger).debug(contains("Template registered successfully"), eq(templateKey), eq(engine));
  }

  @Test
  void registerTemplateFromFile_shouldReturnTrue_whenRegistrationSucceeds() throws Exception {
    // Arrange
    String templateKey = "test-template";
    Path templatePath = tempDir.resolve("template.ftl");
    Files.writeString(templatePath, "Hello, ${name}!");
    TemplateInfo mockTemplate = mock(TemplateInfo.class);

    when(templatePort.registerTemplateFromFile(templateKey, templatePath))
        .thenReturn(TemplateResult.success("Template registered successfully", mockTemplate));

    // Act
    boolean result = service.registerTemplateFromFile(templateKey, templatePath);

    // Assert
    assertTrue(result);
    verify(logger)
        .debug(contains("Registering template from file"), eq(templateKey), eq(templatePath));
    verify(logger)
        .debug(contains("Template registered successfully"), eq(templateKey), eq(templatePath));
  }

  @Test
  void registerTemplateFromFile_shouldReturnFalse_whenRegistrationFails() throws Exception {
    // Arrange
    String templateKey = "test-template";
    Path templatePath = tempDir.resolve("non-existent.ftl");

    when(templatePort.registerTemplateFromFile(templateKey, templatePath))
        .thenReturn(TemplateResult.failure("File not found", "FILE_NOT_FOUND"));

    // Act
    boolean result = service.registerTemplateFromFile(templateKey, templatePath);

    // Assert
    assertFalse(result);
    verify(logger)
        .debug(contains("Registering template from file"), eq(templateKey), eq(templatePath));
    verify(logger)
        .warn(
            contains("Failed to register template"),
            eq(templateKey),
            eq(templatePath),
            anyString());
  }

  @Test
  void registerTemplatesFromDirectory_shouldReturnTemplates_whenRegistrationSucceeds() {
    // Arrange
    Path directory = tempDir;
    String filePattern = "*.ftl";
    Map<String, TemplateInfo> templates = new HashMap<>();
    TemplateInfo template1 = mock(TemplateInfo.class);
    TemplateInfo template2 = mock(TemplateInfo.class);
    templates.put("template1", template1);
    templates.put("template2", template2);

    when(templatePort.registerTemplatesFromDirectory(directory, filePattern))
        .thenReturn(
            TemplateRegistrationResult.success(
                "Templates registered successfully", templates, Collections.emptyMap()));

    // Act
    Map<String, TemplateInfo> result =
        service.registerTemplatesFromDirectory(directory, filePattern);

    // Assert
    assertEquals(templates, result);
    verify(logger)
        .debug(contains("Registering templates from directory"), eq(directory), eq(filePattern));
    verify(logger).debug(contains("Successfully registered"), eq(templates.size()), eq(directory));
  }

  @Test
  void registerTemplatesFromDirectory_shouldReturnEmptyMap_whenDirectoryNotFound() {
    // Arrange
    Path directory = tempDir.resolve("non-existent");
    String filePattern = "*.ftl";

    when(templatePort.registerTemplatesFromDirectory(directory, filePattern))
        .thenReturn(
            TemplateRegistrationResult.failure("Directory not found", "DIRECTORY_NOT_FOUND"));

    // Act
    Map<String, TemplateInfo> result =
        service.registerTemplatesFromDirectory(directory, filePattern);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger)
        .debug(contains("Registering templates from directory"), eq(directory), eq(filePattern));
    verify(logger).warn(contains("Failed to register templates"), eq(directory), anyString());
  }

  @Test
  void hasTemplate_shouldReturnTrue_whenTemplateExists() {
    // Arrange
    String templateKey = "test-template";
    when(templatePort.hasTemplate(templateKey)).thenReturn(true);

    // Act
    boolean result = service.hasTemplate(templateKey);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Checking if template exists"), eq(templateKey));
    verify(logger).debug(contains("Template"), eq(templateKey), eq("exists"));
  }

  @Test
  void hasTemplate_shouldReturnFalse_whenTemplateDoesNotExist() {
    // Arrange
    String templateKey = "non-existent";
    when(templatePort.hasTemplate(templateKey)).thenReturn(false);

    // Act
    boolean result = service.hasTemplate(templateKey);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Checking if template exists"), eq(templateKey));
    verify(logger).debug(contains("Template"), eq(templateKey), eq("does not exist"));
  }

  @Test
  void getTemplate_shouldReturnTemplate_whenTemplateExists() {
    // Arrange
    String templateKey = "test-template";
    TemplateInfo mockTemplate = mock(TemplateInfo.class);

    when(templatePort.getTemplate(templateKey))
        .thenReturn(TemplateResult.success("Template retrieved successfully", mockTemplate));

    // Act
    Optional<TemplateInfo> result = service.getTemplate(templateKey);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockTemplate, result.get());
    verify(logger).debug(contains("Getting template"), eq(templateKey));
    verify(logger).debug(contains("Template retrieved successfully"), eq(templateKey));
  }

  @Test
  void getTemplate_shouldReturnEmpty_whenTemplateDoesNotExist() {
    // Arrange
    String templateKey = "non-existent";

    when(templatePort.getTemplate(templateKey))
        .thenReturn(TemplateResult.failure("Template not found", "TEMPLATE_NOT_FOUND"));

    // Act
    Optional<TemplateInfo> result = service.getTemplate(templateKey);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Getting template"), eq(templateKey));
    verify(logger).warn(contains("Failed to get template"), eq(templateKey), anyString());
  }

  @Test
  void removeTemplate_shouldReturnTrue_whenTemplateRemoved() {
    // Arrange
    String templateKey = "test-template";

    when(templatePort.removeTemplate(templateKey))
        .thenReturn(OperationResult.success("Template removed successfully"));

    // Act
    boolean result = service.removeTemplate(templateKey);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Removing template"), eq(templateKey));
    verify(logger).debug(contains("Template removed successfully"), eq(templateKey));
  }

  @Test
  void removeTemplate_shouldReturnFalse_whenTemplateNotFound() {
    // Arrange
    String templateKey = "non-existent";

    when(templatePort.removeTemplate(templateKey))
        .thenReturn(OperationResult.failure("Template not found", "TEMPLATE_NOT_FOUND"));

    // Act
    boolean result = service.removeTemplate(templateKey);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Removing template"), eq(templateKey));
    verify(logger).warn(contains("Failed to remove template"), eq(templateKey), anyString());
  }

  @Test
  void getTemplateEngine_shouldReturnEngine_whenTemplateExists() {
    // Arrange
    String templateKey = "test-template";
    String engine = "freemarker";

    when(templatePort.getTemplateEngine(templateKey)).thenReturn(Optional.of(engine));

    // Act
    Optional<String> result = service.getTemplateEngine(templateKey);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(engine, result.get());
    verify(logger).debug(contains("Getting engine for template"), eq(templateKey));
    verify(logger).debug(contains("Engine for template"), eq(templateKey), eq(engine));
  }

  @Test
  void getTemplateEngine_shouldReturnEmpty_whenTemplateDoesNotExist() {
    // Arrange
    String templateKey = "non-existent";

    when(templatePort.getTemplateEngine(templateKey)).thenReturn(Optional.empty());

    // Act
    Optional<String> result = service.getTemplateEngine(templateKey);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Getting engine for template"), eq(templateKey));
    verify(logger).debug(contains("No engine found"), eq(templateKey));
  }

  @Test
  void renderTemplate_shouldReturnRenderedContent_whenRenderingSucceeds() {
    // Arrange
    String templateKey = "test-template";
    Map<String, Object> dataModel = Map.of("name", "World");
    String renderedContent = "Hello, World!";

    when(templatePort.renderTemplate(eq(templateKey), eq(dataModel)))
        .thenReturn(RenderResult.success("Template rendered successfully", renderedContent));

    // Act
    Optional<String> result = service.renderTemplate(templateKey, dataModel);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(renderedContent, result.get());
    verify(logger).debug(contains("Rendering template"), eq(templateKey));
    verify(logger).debug(contains("Template rendered successfully"), eq(templateKey));
  }

  @Test
  void renderTemplate_shouldReturnEmpty_whenRenderingFails() {
    // Arrange
    String templateKey = "test-template";
    Map<String, Object> dataModel = Map.of("name", "World");

    when(templatePort.renderTemplate(eq(templateKey), eq(dataModel)))
        .thenReturn(RenderResult.failure("Template not found", "TEMPLATE_NOT_FOUND"));

    // Act
    Optional<String> result = service.renderTemplate(templateKey, dataModel);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Rendering template"), eq(templateKey));
    verify(logger).warn(contains("Failed to render template"), eq(templateKey), anyString());
  }

  @Test
  void renderString_shouldReturnRenderedContent_whenRenderingSucceeds() {
    // Arrange
    String templateContent = "Hello, ${name}!";
    Map<String, Object> dataModel = Map.of("name", "World");
    String renderedContent = "Hello, World!";

    when(templatePort.renderString(eq(templateContent), eq(dataModel)))
        .thenReturn(RenderResult.success("Template rendered successfully", renderedContent));

    // Act
    Optional<String> result = service.renderString(templateContent, dataModel);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(renderedContent, result.get());
    verify(logger).debug(contains("Rendering template string"));
    verify(logger).debug(contains("Template string rendered successfully"));
  }

  @Test
  void generatePdf_shouldReturnTrue_whenGenerationSucceeds() {
    // Arrange
    String html = "<html><body>Hello, World!</body></html>";
    Path outputPath = tempDir.resolve("output.pdf");

    when(templatePort.generatePdf(eq(html), eq(outputPath)))
        .thenReturn(DocumentResult.successFile("PDF generated successfully", outputPath, 1024));

    // Act
    boolean result = service.generatePdf(html, outputPath);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Generating PDF to"), eq(outputPath));
    verify(logger).debug(contains("PDF generated successfully"), eq(outputPath), eq(1024L));
  }

  @Test
  void generatePdf_shouldReturnFalse_whenGenerationFails() {
    // Arrange
    String html = "<html><body>Hello, World!</body></html>";
    Path outputPath = tempDir.resolve("invalid/output.pdf");

    when(templatePort.generatePdf(eq(html), eq(outputPath)))
        .thenReturn(DocumentResult.failure("Failed to create output directory", "IO_ERROR"));

    // Act
    boolean result = service.generatePdf(html, outputPath);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Generating PDF to"), eq(outputPath));
    verify(logger).warn(contains("Failed to generate PDF"), eq(outputPath), anyString());
  }

  @Test
  void generatePdf_withOutputStream_shouldReturnTrue_whenGenerationSucceeds() {
    // Arrange
    String html = "<html><body>Hello, World!</body></html>";
    OutputStream outputStream = new ByteArrayOutputStream();

    when(templatePort.generatePdf(eq(html), same(outputStream)))
        .thenReturn(DocumentResult.successStream("PDF generated successfully", 1024));

    // Act
    boolean result = service.generatePdf(html, outputStream);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Generating PDF to output stream"));
    verify(logger).debug(contains("PDF generated successfully to output stream"), eq(1024L));
  }

  @Test
  void generateDocument_shouldReturnTrue_whenGenerationSucceeds() {
    // Arrange
    String templateKey = "test-template";
    Map<String, Object> dataModel = Map.of("name", "World");
    OutputFormat outputFormat = OutputFormat.HTML;
    Path outputPath = tempDir.resolve("output.html");

    when(templatePort.generateDocument(
            eq(templateKey), eq(dataModel), eq(outputFormat), eq(outputPath), any()))
        .thenReturn(
            DocumentResult.successFile("Document generated successfully", outputPath, 1024));

    // Act
    boolean result = service.generateDocument(templateKey, dataModel, outputFormat, outputPath);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Generating"), eq(outputFormat), eq(templateKey), eq(outputPath));
    verify(logger)
        .debug(
            contains("document generated successfully"),
            eq(outputFormat),
            eq(templateKey),
            eq(outputPath),
            eq(1024L));
  }

  @Test
  void generateDocument_shouldReturnFalse_whenGenerationFails() {
    // Arrange
    String templateKey = "non-existent";
    Map<String, Object> dataModel = Map.of("name", "World");
    OutputFormat outputFormat = OutputFormat.HTML;
    Path outputPath = tempDir.resolve("output.html");

    when(templatePort.generateDocument(
            eq(templateKey), eq(dataModel), eq(outputFormat), eq(outputPath), any()))
        .thenReturn(DocumentResult.failure("Template not found", "TEMPLATE_NOT_FOUND"));

    // Act
    boolean result = service.generateDocument(templateKey, dataModel, outputFormat, outputPath);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Generating"), eq(outputFormat), eq(templateKey), eq(outputPath));
    verify(logger)
        .warn(
            contains("Failed to generate"),
            eq(outputFormat),
            eq(templateKey),
            eq(outputPath),
            anyString());
  }

  @Test
  void renderAndSaveToFile_shouldReturnTrue_whenOperationSucceeds() throws Exception {
    // Arrange
    String templateKey = "test-template";
    Map<String, Object> dataModel = Map.of("name", "World");
    Path outputPath = tempDir.resolve("output.html");
    String renderedContent = "Hello, World!";

    when(templatePort.renderTemplate(eq(templateKey), eq(dataModel)))
        .thenReturn(RenderResult.success("Template rendered successfully", renderedContent));

    // Act
    boolean result = service.renderAndSaveToFile(templateKey, dataModel, outputPath);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Rendering template"), eq(templateKey), eq(outputPath));
    verify(logger).debug(contains("Rendered content saved"), eq(outputPath));
    assertTrue(Files.exists(outputPath));
    assertEquals(renderedContent, Files.readString(outputPath));
  }

  @Test
  void renderTemplateAsync_shouldCompleteSuccessfully_whenRenderingSucceeds() throws Exception {
    // Arrange
    String templateKey = "test-template";
    Map<String, Object> dataModel = Map.of("name", "World");
    String renderedContent = "Hello, World!";

    when(templatePort.renderTemplate(eq(templateKey), eq(dataModel)))
        .thenReturn(RenderResult.success("Template rendered successfully", renderedContent));

    // Act
    CompletableFuture<String> future = service.renderTemplateAsync(templateKey, dataModel);
    String result = future.get(); // Will throw if the future completes exceptionally

    // Assert
    assertEquals(renderedContent, result);
    verify(logger).debug(contains("Rendering template"), eq(templateKey));
    verify(logger).debug(contains("Template rendered successfully"), eq(templateKey));
  }

  @Test
  void renderTemplateAsync_shouldCompleteExceptionally_whenRenderingFails() {
    // Arrange
    String templateKey = "non-existent";
    Map<String, Object> dataModel = Map.of("name", "World");

    when(templatePort.renderTemplate(eq(templateKey), eq(dataModel)))
        .thenReturn(RenderResult.failure("Template not found", "TEMPLATE_NOT_FOUND"));

    // Act
    CompletableFuture<String> future = service.renderTemplateAsync(templateKey, dataModel);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    verify(logger).debug(contains("Rendering template"), eq(templateKey));
    verify(logger).warn(contains("Failed to render template"), eq(templateKey), anyString());
  }

  @Test
  void generateDocumentAsync_shouldCompleteSuccessfully_whenGenerationSucceeds() throws Exception {
    // Arrange
    String templateKey = "test-template";
    Map<String, Object> dataModel = Map.of("name", "World");
    OutputFormat outputFormat = OutputFormat.HTML;
    Path outputPath = tempDir.resolve("output.html");

    when(templatePort.generateDocument(
            eq(templateKey), eq(dataModel), eq(outputFormat), eq(outputPath), any()))
        .thenReturn(
            DocumentResult.successFile("Document generated successfully", outputPath, 1024));

    // Act
    CompletableFuture<Path> future =
        service.generateDocumentAsync(templateKey, dataModel, outputFormat, outputPath);
    Path result = future.get(); // Will throw if the future completes exceptionally

    // Assert
    assertEquals(outputPath, result);
    verify(logger).debug(contains("Generating"), eq(outputFormat), eq(templateKey), eq(outputPath));
    verify(logger)
        .debug(
            contains("document generated successfully"),
            eq(outputFormat),
            eq(templateKey),
            eq(outputPath),
            eq(1024L));
  }

  @Test
  void generateDocumentAsync_shouldCompleteExceptionally_whenGenerationFails() {
    // Arrange
    String templateKey = "non-existent";
    Map<String, Object> dataModel = Map.of("name", "World");
    OutputFormat outputFormat = OutputFormat.HTML;
    Path outputPath = tempDir.resolve("output.html");

    when(templatePort.generateDocument(
            eq(templateKey), eq(dataModel), eq(outputFormat), eq(outputPath), any()))
        .thenReturn(DocumentResult.failure("Template not found", "TEMPLATE_NOT_FOUND"));

    // Act
    CompletableFuture<Path> future =
        service.generateDocumentAsync(templateKey, dataModel, outputFormat, outputPath);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    verify(logger).debug(contains("Generating"), eq(outputFormat), eq(templateKey), eq(outputPath));
    verify(logger)
        .warn(
            contains("Failed to generate"),
            eq(outputFormat),
            eq(templateKey),
            eq(outputPath),
            anyString());
  }

  @Test
  void pdfOptionsBuilder_shouldCreateOptionsWithDefaults() {
    // Act
    PdfOptions options = service.pdfOptionsBuilder().build();

    // Assert
    assertEquals(PageSize.A4, options.getPageSize());
    assertEquals(PageOrientation.PORTRAIT, options.getOrientation());
    assertEquals(72f, options.getMargins().getTop());
    assertEquals(72f, options.getMargins().getRight());
    assertEquals(72f, options.getMargins().getBottom());
    assertEquals(72f, options.getMargins().getLeft());
    assertTrue(options.isCompressed());
    assertFalse(options.isPdfA());
    assertFalse(options.includeTableOfContents());
    assertFalse(options.getBaseUrl().isPresent());
    assertTrue(options.getMetadata().isEmpty());
  }

  @Test
  void pdfOptionsBuilder_shouldCreateCustomizedOptions() {
    // Arrange
    Map<String, String> metadata = Map.of("Author", "Test Author", "Title", "Test Document");

    // Act
    PdfOptions options =
        service
            .pdfOptionsBuilder()
            .pageSize(PageSize.LETTER)
            .orientation(PageOrientation.LANDSCAPE)
            .margins(service.createMargins(36, 36, 36, 36))
            .compressed(false)
            .pdfA(true)
            .includeTableOfContents(true)
            .baseUrl("http://example.com")
            .metadata(metadata)
            .build();

    // Assert
    assertEquals(PageSize.LETTER, options.getPageSize());
    assertEquals(PageOrientation.LANDSCAPE, options.getOrientation());
    assertEquals(36f, options.getMargins().getTop());
    assertEquals(36f, options.getMargins().getRight());
    assertEquals(36f, options.getMargins().getBottom());
    assertEquals(36f, options.getMargins().getLeft());
    assertFalse(options.isCompressed());
    assertTrue(options.isPdfA());
    assertTrue(options.includeTableOfContents());
    assertTrue(options.getBaseUrl().isPresent());
    assertEquals("http://example.com", options.getBaseUrl().get());
    assertEquals(metadata, options.getMetadata());
  }

  @Test
  void createMargins_shouldCreateMarginsWithSpecifiedValues() {
    // Act
    Margins margins = service.createMargins(10, 20, 30, 40);

    // Assert
    assertEquals(10f, margins.getTop());
    assertEquals(20f, margins.getRight());
    assertEquals(30f, margins.getBottom());
    assertEquals(40f, margins.getLeft());
  }

  @Test
  void createDocumentOptions_shouldCreateOptionsWithMetadata() {
    // Arrange
    Map<String, String> metadata = Map.of("Author", "Test Author", "Title", "Test Document");

    // Act
    DocumentOptions options = service.createDocumentOptions(metadata);

    // Assert
    assertEquals(metadata, options.getMetadata());
  }
}
