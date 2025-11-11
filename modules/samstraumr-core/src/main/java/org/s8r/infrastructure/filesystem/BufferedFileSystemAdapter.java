/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.filesystem;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Optimized implementation of the FileSystemPort interface with buffering.
 * 
 * <p>This adapter provides enhanced file system operations with:
 * - Buffered read/write operations for improved performance
 * - File content caching for frequently accessed files
 * - Path-based locking for concurrent access optimization
 * - NIO-based operations for efficient I/O
 * - Comprehensive performance metrics tracking
 */
public class BufferedFileSystemAdapter implements FileSystemPort {

    private static final int DEFAULT_BUFFER_SIZE = 8192; // 8KB
    private static final int LARGE_BUFFER_SIZE = 64 * 1024; // 64KB for sequential reads
    private static final int MAX_CACHED_FILE_SIZE = 1024 * 1024; // 1MB max for caching
    private static final int DEFAULT_CACHE_SIZE = 100; // Default number of files to cache
    
    private final LoggerPort logger;
    private final Map<Path, ReadWriteLock> fileLocks;
    private final Map<Path, byte[]> fileCache;
    private final Map<Path, Instant> cacheTimestamps;
    private final ExecutorService asyncExecutor;
    private final int bufferSize;
    private final int maxCachedFiles;
    
    // Performance metrics
    private final AtomicLong totalReads = new AtomicLong(0);
    private final AtomicLong totalWrites = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong asyncOperations = new AtomicLong(0);
    private final AtomicLong dirOperations = new AtomicLong(0);
    private final AtomicLong totalBytesRead = new AtomicLong(0);
    private final AtomicLong totalBytesWritten = new AtomicLong(0);
    
    /**
     * Creates a BufferedFileSystemAdapter with default settings and logger.
     *
     * @param logger The logger to use
     */
    public BufferedFileSystemAdapter(LoggerPort logger) {
        this(logger, DEFAULT_BUFFER_SIZE, DEFAULT_CACHE_SIZE);
    }
    
    /**
     * Creates a BufferedFileSystemAdapter with custom buffer size and cache capacity.
     *
     * @param logger The logger to use
     * @param bufferSize The buffer size for read/write operations
     * @param maxCachedFiles The maximum number of files to cache
     */
    public BufferedFileSystemAdapter(LoggerPort logger, int bufferSize, int maxCachedFiles) {
        this.logger = logger;
        this.fileLocks = new ConcurrentHashMap<>();
        this.fileCache = new ConcurrentHashMap<>();
        this.cacheTimestamps = new ConcurrentHashMap<>();
        this.asyncExecutor = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                new ThreadFactory() {
                    private final AtomicLong counter = new AtomicLong(0);
                    
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "filesystem-worker-" + counter.incrementAndGet());
                        thread.setDaemon(true);
                        return thread;
                    }
                });
        this.bufferSize = bufferSize;
        this.maxCachedFiles = maxCachedFiles;
        
        logger.info("BufferedFileSystemAdapter initialized with bufferSize={}, maxCachedFiles={}", 
                bufferSize, maxCachedFiles);
    }
    
    @Override
    public FileResult initialize() {
        logger.info("BufferedFileSystemAdapter initialized successfully");
        return new SimpleFileResult(true, "BufferedFileSystemAdapter initialized successfully");
    }
    
    @Override
    public FileResult shutdown() {
        logger.info("Shutting down BufferedFileSystemAdapter");
        try {
            // Shutdown the executor service
            asyncExecutor.shutdown();
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
                logger.warn("Forced shutdown of file system executor after timeout");
            }
            
            // Clear caches
            fileCache.clear();
            cacheTimestamps.clear();
            fileLocks.clear();
            
            logger.info("BufferedFileSystemAdapter shut down successfully");
            return new SimpleFileResult(true, "BufferedFileSystemAdapter shut down successfully");
        } catch (InterruptedException e) {
            logger.error("Error during shutdown of BufferedFileSystemAdapter", e);
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            return new SimpleFileResult(false, "Error during shutdown", e.getMessage());
        }
    }

    @Override
    public Optional<String> readFile(Path path) throws IOException {
        try {
            byte[] content = readFileBytes(path.toString());
            return Optional.of(new String(content));
        } catch (IOException e) {
            logger.error("Error reading file: {}", path, e);
            throw e;
        }
    }
    
    /**
     * Reads a file as bytes using optimized buffered methods.
     *
     * @param pathStr The path to the file
     * @return The file content as bytes
     * @throws IOException If an I/O error occurs
     */
    private byte[] readFileBytes(String pathStr) throws IOException {
        Path filePath = Path.of(pathStr);
        
        // Try to get from cache first
        byte[] cachedContent = checkCache(filePath);
        if (cachedContent != null) {
            cacheHits.incrementAndGet();
            totalReads.incrementAndGet();
            logger.debug("Cache hit for file: {}", filePath);
            return cachedContent;
        }
        
        cacheMisses.incrementAndGet();
        
        // Get or create a lock for this file
        ReadWriteLock fileLock = getFileLock(filePath);
        fileLock.readLock().lock();
        
        try {
            if (!Files.exists(filePath)) {
                logger.warn("File not found: {}", filePath);
                throw new IOException("File not found: " + filePath);
            }
            
            // Check if file is larger than threshold for efficient reading
            long fileSize = Files.size(filePath);
            byte[] content;
            
            if (fileSize > LARGE_BUFFER_SIZE) {
                // Use FileChannel for larger files
                logger.debug("Using FileChannel for large file: {} ({} bytes)", filePath, fileSize);
                content = readWithFileChannel(filePath, fileSize);
            } else {
                // Use simple read for smaller files
                logger.debug("Reading small file: {} ({} bytes)", filePath, fileSize);
                content = Files.readAllBytes(filePath);
            }
            
            totalReads.incrementAndGet();
            totalBytesRead.addAndGet(content.length);
            
            // Cache the file content if it's not too large
            if (fileSize <= MAX_CACHED_FILE_SIZE) {
                updateCache(filePath, content);
            }
            
            return content;
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
            throw e;
        } finally {
            fileLock.readLock().unlock();
        }
    }
    
    /**
     * Optimized file reading using NIO FileChannel.
     *
     * @param filePath The path to the file
     * @param fileSize The known file size
     * @return The file content as a byte array
     * @throws IOException If an I/O error occurs
     */
    private byte[] readWithFileChannel(Path filePath, long fileSize) throws IOException {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            // Determine optimal buffer size based on file size
            int optimalBufferSize = fileSize > 10 * LARGE_BUFFER_SIZE ? LARGE_BUFFER_SIZE : bufferSize;
            ByteBuffer buffer = ByteBuffer.allocateDirect(optimalBufferSize);
            ByteArrayOutputStream output = new ByteArrayOutputStream((int) fileSize);
            
            int bytesRead;
            while ((bytesRead = channel.read(buffer)) != -1) {
                buffer.flip();
                byte[] temp = new byte[bytesRead];
                buffer.get(temp);
                output.write(temp);
                buffer.clear();
            }
            
            return output.toByteArray();
        } catch (IOException e) {
            logger.error("Error reading file with channel: {}", filePath, e);
            throw e;
        }
    }

    @Override
    public boolean writeFile(Path path, String content) throws IOException {
        try {
            writeFileBytes(path.toString(), content.getBytes());
            return Files.exists(path);
        } catch (IOException e) {
            logger.error("Error writing file: {}", path, e);
            throw e;
        }
    }
    
    /**
     * Writes bytes to a file using optimized buffered methods.
     *
     * @param pathStr The path to the file
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    private void writeFileBytes(String pathStr, byte[] content) throws IOException {
        Path filePath = Path.of(pathStr);
        
        // Make sure parent directories exist
        Path parent = filePath.getParent();
        if (parent != null && !Files.exists(parent)) {
            logger.debug("Creating parent directories for: {}", filePath);
            Files.createDirectories(parent);
            dirOperations.incrementAndGet();
        }
        
        // Get or create a lock for this file
        ReadWriteLock fileLock = getFileLock(filePath);
        fileLock.writeLock().lock();
        
        try {
            // For larger files, use FileChannel for better performance
            if (content.length > LARGE_BUFFER_SIZE) {
                logger.debug("Using FileChannel for large file write: {} ({} bytes)", filePath, content.length);
                writeWithFileChannel(filePath, content);
            } else {
                // Use atomic write for smaller files
                logger.debug("Writing small file: {} ({} bytes)", filePath, content.length);
                Files.write(filePath, content);
            }
            
            totalWrites.incrementAndGet();
            totalBytesWritten.addAndGet(content.length);
            
            // Update the cache if applicable
            if (content.length <= MAX_CACHED_FILE_SIZE) {
                updateCache(filePath, content);
            } else {
                // Remove from cache if it was previously cached
                removeFromCache(filePath);
            }
        } catch (IOException e) {
            logger.error("Error writing file: {}", filePath, e);
            throw e;
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Optimized file writing using NIO FileChannel.
     *
     * @param filePath The path to the file
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    private void writeWithFileChannel(Path filePath, byte[] content) throws IOException {
        try (FileChannel channel = FileChannel.open(filePath, 
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            // Determine optimal buffer size
            int optimalBufferSize = Math.min(LARGE_BUFFER_SIZE, content.length);
            ByteBuffer buffer = ByteBuffer.allocateDirect(optimalBufferSize);
            
            int position = 0;
            while (position < content.length) {
                int remaining = content.length - position;
                int chunkSize = Math.min(optimalBufferSize, remaining);
                
                buffer.clear();
                buffer.put(content, position, chunkSize);
                buffer.flip();
                
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                
                position += chunkSize;
            }
            
            // Ensure data is flushed to disk
            channel.force(true);
        } catch (IOException e) {
            logger.error("Error writing file with channel: {}", filePath, e);
            throw e;
        }
    }

    @Override
    public boolean fileExists(Path path) {
        boolean exists = Files.exists(path) && Files.isRegularFile(path);
        if (logger.isDebugEnabled()) {
            logger.debug("Checking if file exists: {} - {}", path, exists);
        }
        return exists;
    }

    @Override
    public boolean createDirectory(Path path) throws IOException {
        try {
            Files.createDirectories(path);
            dirOperations.incrementAndGet();
            boolean created = Files.exists(path) && Files.isDirectory(path);
            if (created) {
                logger.debug("Created directory: {}", path);
            } else {
                logger.warn("Failed to create directory: {}", path);
            }
            return created;
        } catch (IOException e) {
            logger.error("Error creating directory: {}", path, e);
            throw e;
        }
    }
    
    @Override
    public FileResult createDirectory(String path) {
        try {
            boolean created = createDirectory(Path.of(path));
            return new SimpleFileResult(created, 
                created ? "Directory created successfully" : "Failed to create directory");
        } catch (IOException e) {
            logger.error("Error creating directory: {}", path, e);
            return new SimpleFileResult(false, "Failed to create directory", e.getMessage());
        }
    }

    @Override
    public List<Path> listFiles(Path path) throws IOException {
        dirOperations.incrementAndGet();
        
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            logger.warn("Directory not found or not a directory: {}", path);
            return List.of();
        }
        
        try {
            List<Path> files = Files.list(path)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
            
            logger.debug("Listed {} files in directory: {}", files.size(), path);
            return files;
        } catch (IOException e) {
            logger.error("Error listing files in directory: {}", path, e);
            throw e;
        }
    }

    @Override
    public boolean delete(Path path) throws IOException {
        if (!Files.exists(path)) {
            logger.warn("Cannot delete non-existent path: {}", path);
            return false;
        }
        
        // Get or create a lock for this file
        ReadWriteLock fileLock = getFileLock(path);
        fileLock.writeLock().lock();
        
        try {
            if (Files.isDirectory(path)) {
                logger.debug("Deleting directory: {}", path);
                // Check if directory is empty
                try (Stream<Path> entries = Files.list(path)) {
                    if (entries.findFirst().isPresent()) {
                        // Delete recursively
                        logger.debug("Directory not empty, deleting recursively: {}", path);
                        Files.walk(path)
                            .sorted((p1, p2) -> -p1.compareTo(p2))
                            .forEach(p -> {
                                try {
                                    Files.delete(p);
                                    // Clean up locks for deleted files
                                    fileLocks.remove(p);
                                    // Remove from cache
                                    removeFromCache(p);
                                } catch (IOException e) {
                                    throw new RuntimeException("Failed to delete: " + p, e);
                                }
                            });
                    } else {
                        // Empty directory can be deleted directly
                        logger.debug("Directory empty, deleting directly: {}", path);
                        Files.delete(path);
                    }
                }
            } else {
                logger.debug("Deleting file: {}", path);
                Files.delete(path);
                
                // Remove from cache if it was previously cached
                removeFromCache(path);
            }
            
            // Clean up the lock when no longer needed
            fileLocks.remove(path);
            
            boolean deleted = !Files.exists(path);
            if (deleted) {
                logger.debug("Successfully deleted: {}", path);
            } else {
                logger.warn("Failed to delete: {}", path);
            }
            return deleted;
        } catch (IOException e) {
            logger.error("Error deleting path: {}", path, e);
            throw e;
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean exists(String path) {
        if (path == null) {
            return false;
        }
        
        Path filePath = Path.of(path);
        boolean exists = Files.exists(filePath);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Checking if path exists: {} - {}", path, exists);
        }
        
        return exists;
    }
    
    @Override
    public FileResult readString(String path, Charset charset) {
        try {
            byte[] content = readFileBytes(path);
            String stringContent = new String(content, charset);
            return new SimpleFileResult(true, "File read successfully", 
                    Map.of("content", stringContent));
        } catch (IOException e) {
            logger.error("Error reading file: {}", path, e);
            return new SimpleFileResult(false, "Failed to read file", e.getMessage());
        }
    }
    
    @Override
    public FileResult readLines(String path, Charset charset) {
        try {
            byte[] content = readFileBytes(path);
            String stringContent = new String(content, charset);
            List<String> lines = List.of(stringContent.split("\\n"));
            return new SimpleFileResult(true, "File read successfully", 
                    Map.of("lines", lines));
        } catch (IOException e) {
            logger.error("Error reading file lines: {}", path, e);
            return new SimpleFileResult(false, "Failed to read file", e.getMessage());
        }
    }
    
    @Override
    public FileResult readLines(String path, Charset charset, long start, long count) {
        try {
            byte[] content = readFileBytes(path);
            String stringContent = new String(content, charset);
            List<String> allLines = List.of(stringContent.split("\\n"));
            int startIdx = (int) start;
            int endIdx = Math.min(startIdx + (int) count, allLines.size());
            
            if (startIdx >= allLines.size()) {
                logger.warn("Start index {} out of bounds for file with {} lines: {}", 
                        startIdx, allLines.size(), path);
                return new SimpleFileResult(false, "Failed to read lines", "Start index out of bounds");
            }
            
            List<String> lines = allLines.subList(startIdx, endIdx);
            return new SimpleFileResult(true, "File lines read successfully", 
                    Map.of("lines", lines));
        } catch (IOException e) {
            logger.error("Error reading file lines with range: {}", path, e);
            return new SimpleFileResult(false, "Failed to read file", e.getMessage());
        }
    }
    
    @Override
    public FileResult writeString(String path, String content, Charset charset, boolean append) {
        try {
            Path filePath = Path.of(path);
            
            if (append && Files.exists(filePath)) {
                logger.debug("Appending to file: {}", path);
                ReadWriteLock fileLock = getFileLock(filePath);
                fileLock.writeLock().lock();
                
                try {
                    byte[] existingContent = Files.readAllBytes(filePath);
                    byte[] newContent = content.getBytes(charset);
                    byte[] combinedContent = new byte[existingContent.length + newContent.length];
                    
                    System.arraycopy(existingContent, 0, combinedContent, 0, existingContent.length);
                    System.arraycopy(newContent, 0, combinedContent, existingContent.length, newContent.length);
                    
                    writeFileBytes(path, combinedContent);
                } finally {
                    fileLock.writeLock().unlock();
                }
            } else {
                writeFileBytes(path, content.getBytes(charset));
            }
            
            return new SimpleFileResult(true, "File written successfully");
        } catch (IOException e) {
            logger.error("Error writing string to file: {}", path, e);
            return new SimpleFileResult(false, "Failed to write file", e.getMessage());
        }
    }
    
    @Override
    public FileResult writeLines(String path, List<String> lines, Charset charset, boolean append) {
        try {
            String content = String.join("\n", lines);
            return writeString(path, content, charset, append);
        } catch (Exception e) {
            logger.error("Error writing lines to file: {}", path, e);
            return new SimpleFileResult(false, "Failed to write file lines", e.getMessage());
        }
    }
    
    @Override
    public FileResult copy(String source, String destination) {
        Path sourcePath = Path.of(source);
        Path destPath = Path.of(destination);
        
        // Make sure parent directories exist
        Path parent = destPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                logger.debug("Creating parent directories for destination: {}", destPath);
                Files.createDirectories(parent);
                dirOperations.incrementAndGet();
            } catch (IOException e) {
                logger.error("Error creating parent directories for copy destination: {}", destPath, e);
                return new SimpleFileResult(false, "Failed to create destination directory", e.getMessage());
            }
        }
        
        // Get or create locks for both files
        ReadWriteLock sourceLock = getFileLock(sourcePath);
        ReadWriteLock destLock = getFileLock(destPath);
        
        // Acquire locks in a consistent order to prevent deadlocks
        if (sourcePath.compareTo(destPath) < 0) {
            sourceLock.readLock().lock();
            destLock.writeLock().lock();
        } else {
            destLock.writeLock().lock();
            sourceLock.readLock().lock();
        }
        
        try {
            // Check if source file is in cache
            byte[] cachedContent = checkCache(sourcePath);
            if (cachedContent != null) {
                // Write cached content to destination
                logger.debug("Using cached content for copy from {} to {}", sourcePath, destPath);
                try {
                    Files.write(destPath, cachedContent);
                    cacheHits.incrementAndGet();
                } catch (IOException e) {
                    logger.error("Error writing cached content to destination: {}", destPath, e);
                    return new SimpleFileResult(false, "Failed to copy file", e.getMessage());
                }
            } else {
                cacheMisses.incrementAndGet();
                
                if (!Files.exists(sourcePath)) {
                    logger.warn("Source file not found: {}", sourcePath);
                    return new SimpleFileResult(false, "Failed to copy file", "Source file not found");
                }
                
                try {
                    // Use optimized copy if file is large
                    long fileSize = Files.size(sourcePath);
                    if (fileSize > LARGE_BUFFER_SIZE) {
                        logger.debug("Using FileChannel for large file copy: {} to {} ({} bytes)", 
                                sourcePath, destPath, fileSize);
                        copyWithFileChannel(sourcePath, destPath);
                    } else {
                        // Use built-in copy for smaller files
                        logger.debug("Copying small file: {} to {} ({} bytes)", 
                                sourcePath, destPath, fileSize);
                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    logger.error("Error during file copy: {} to {}", sourcePath, destPath, e);
                    return new SimpleFileResult(false, "Failed to copy file", e.getMessage());
                }
            }
            
            totalReads.incrementAndGet();
            totalWrites.incrementAndGet();
            
            // Update cache for destination
            try {
                byte[] content = Files.readAllBytes(destPath);
                if (content.length <= MAX_CACHED_FILE_SIZE) {
                    updateCache(destPath, content);
                }
                totalBytesRead.addAndGet(content.length);
                totalBytesWritten.addAndGet(content.length);
            } catch (IOException e) {
                // Just log the error but don't fail the operation
                logger.warn("Error updating cache after copy: {}", destPath, e);
            }
            
            return new SimpleFileResult(true, "File copied successfully");
        } finally {
            if (sourcePath.compareTo(destPath) < 0) {
                destLock.writeLock().unlock();
                sourceLock.readLock().unlock();
            } else {
                sourceLock.readLock().unlock();
                destLock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Optimized file copying using NIO FileChannel.
     *
     * @param source The source path
     * @param destination The destination path
     * @throws IOException If an I/O error occurs
     */
    private void copyWithFileChannel(Path source, Path destination) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel destinationChannel = FileChannel.open(destination, 
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            long size = sourceChannel.size();
            long position = 0;
            long transferred;
            while (position < size) {
                // Use efficient transferTo method which can use zero-copy on some platforms
                transferred = sourceChannel.transferTo(position, LARGE_BUFFER_SIZE, destinationChannel);
                if (transferred > 0) {
                    position += transferred;
                }
            }
            
            // Ensure data is flushed to disk
            destinationChannel.force(true);
        }
    }
    
    @Override
    public FileResult copyDirectory(String source, String destination) {
        try {
            Path sourcePath = Path.of(source);
            Path destPath = Path.of(destination);
            
            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
                logger.warn("Source directory not found: {}", sourcePath);
                return new SimpleFileResult(false, "Failed to copy directory", "Source directory not found");
            }
            
            // Create destination directory
            Files.createDirectories(destPath);
            dirOperations.incrementAndGet();
            
            // Copy all files recursively
            try (Stream<Path> stream = Files.walk(sourcePath)) {
                stream.forEach(src -> {
                    try {
                        Path dest = destPath.resolve(sourcePath.relativize(src));
                        if (Files.isDirectory(src)) {
                            if (!Files.exists(dest)) {
                                Files.createDirectory(dest);
                                dirOperations.incrementAndGet();
                            }
                            return;
                        }
                        
                        // Copy the file using our optimized copying method
                        copyWithFileChannel(src, dest);
                        totalReads.incrementAndGet();
                        totalWrites.incrementAndGet();
                        
                        // Update cache for destination
                        try {
                            byte[] content = Files.readAllBytes(dest);
                            if (content.length <= MAX_CACHED_FILE_SIZE) {
                                updateCache(dest, content);
                            }
                            totalBytesRead.addAndGet(content.length);
                            totalBytesWritten.addAndGet(content.length);
                        } catch (IOException e) {
                            // Just log the error but don't fail the operation
                            logger.warn("Error updating cache after copy: {}", dest, e);
                        }
                    } catch (IOException e) {
                        logger.error("Error copying file during directory copy: {}", src, e);
                        throw new RuntimeException(e);
                    }
                });
            }
            
            logger.info("Directory copied successfully: {} to {}", sourcePath, destPath);
            return new SimpleFileResult(true, "Directory copied successfully");
        } catch (Exception e) {
            logger.error("Error copying directory: {} to {}", source, destination, e);
            return new SimpleFileResult(false, "Failed to copy directory", e.getMessage());
        }
    }
    
    @Override
    public FileResult move(String source, String destination) {
        Path sourcePath = Path.of(source);
        Path destPath = Path.of(destination);
        
        // Make sure parent directories exist
        Path parent = destPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                logger.debug("Creating parent directories for move destination: {}", destPath);
                Files.createDirectories(parent);
                dirOperations.incrementAndGet();
            } catch (IOException e) {
                logger.error("Error creating parent directories for move destination: {}", destPath, e);
                return new SimpleFileResult(false, "Failed to create destination directory", e.getMessage());
            }
        }
        
        // Get or create locks for both files
        ReadWriteLock sourceLock = getFileLock(sourcePath);
        ReadWriteLock destLock = getFileLock(destPath);
        
        // Acquire locks in a consistent order to prevent deadlocks
        if (sourcePath.compareTo(destPath) < 0) {
            sourceLock.writeLock().lock();
            destLock.writeLock().lock();
        } else {
            destLock.writeLock().lock();
            sourceLock.writeLock().lock();
        }
        
        try {
            if (!Files.exists(sourcePath)) {
                logger.warn("Source file not found: {}", sourcePath);
                return new SimpleFileResult(false, "Failed to move file", "Source file not found");
            }
            
            // Try atomic move first (more efficient if possible)
            try {
                logger.debug("Attempting atomic move: {} to {}", sourcePath, destPath);
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // If atomic move fails (e.g., across filesystems), fallback to copy and delete
                logger.debug("Atomic move failed, fallback to copy-delete: {} to {}", sourcePath, destPath);
                Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(sourcePath);
            }
            
            totalReads.incrementAndGet();
            totalWrites.incrementAndGet();
            
            // Update cache - remove source, update destination if needed
            byte[] content = checkCache(sourcePath);
            if (content != null) {
                updateCache(destPath, content);
            }
            removeFromCache(sourcePath);
            
            // Clean up source lock
            fileLocks.remove(sourcePath);
            
            logger.info("File moved successfully: {} to {}", sourcePath, destPath);
            return new SimpleFileResult(true, "File moved successfully");
        } catch (IOException e) {
            logger.error("Error moving file: {} to {}", sourcePath, destPath, e);
            return new SimpleFileResult(false, "Failed to move file", e.getMessage());
        } finally {
            if (sourcePath.compareTo(destPath) < 0) {
                destLock.writeLock().unlock();
                sourceLock.writeLock().unlock();
            } else {
                sourceLock.writeLock().unlock();
                destLock.writeLock().unlock();
            }
        }
    }
    
    @Override
    public long size(String path) {
        try {
            Path filePath = Path.of(path);
            long size = Files.size(filePath);
            logger.debug("File size: {} - {} bytes", filePath, size);
            return size;
        } catch (IOException e) {
            logger.error("Error getting file size: {}", path, e);
            return -1;
        }
    }
    
    @Override
    public Optional<Instant> getLastModifiedTime(String path) {
        try {
            Path filePath = Path.of(path);
            Instant lastModified = Files.getLastModifiedTime(filePath).toInstant();
            logger.debug("Last modified time: {} - {}", filePath, lastModified);
            return Optional.of(lastModified);
        } catch (IOException e) {
            logger.error("Error getting last modified time: {}", path, e);
            return Optional.empty();
        }
    }
    
    @Override
    public FileResult setLastModifiedTime(String path, Instant time) {
        try {
            Path filePath = Path.of(path);
            Files.setLastModifiedTime(filePath, FileTime.from(time));
            logger.debug("Last modified time set: {} - {}", filePath, time);
            return new SimpleFileResult(true, "Last modified time set successfully");
        } catch (IOException e) {
            logger.error("Error setting last modified time: {}", path, e);
            return new SimpleFileResult(false, "Failed to set last modified time", e.getMessage());
        }
    }
    
    @Override
    public FileResult list(String directory) {
        try {
            Path dirPath = Path.of(directory);
            dirOperations.incrementAndGet();
            
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                logger.warn("Directory not found or not a directory: {}", dirPath);
                return new SimpleFileResult(false, "Failed to list directory", "Path is not a directory");
            }
            
            List<FileInfo> fileInfos = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
                for (Path file : stream) {
                    fileInfos.add(createFileInfo(file));
                }
            }
            
            logger.debug("Listed {} entries in directory: {}", fileInfos.size(), dirPath);
            return new SimpleFileResult(true, "Directory listed successfully", Map.of("files", fileInfos));
        } catch (IOException e) {
            logger.error("Error listing directory: {}", directory, e);
            return new SimpleFileResult(false, "Failed to list directory", e.getMessage());
        }
    }
    
    @Override
    public FileResult search(String directory, String pattern, SearchMode mode) {
        try {
            Path dirPath = Path.of(directory);
            dirOperations.incrementAndGet();
            
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                logger.warn("Directory not found or not a directory for search: {}", dirPath);
                return new SimpleFileResult(false, "Failed to search directory", "Path is not a directory");
            }
            
            List<FileInfo> matchingFiles = new ArrayList<>();
            
            if (mode == SearchMode.CURRENT_DIRECTORY) {
                // Only search current directory
                logger.debug("Searching current directory only: {}", dirPath);
                try (Stream<Path> paths = Files.list(dirPath)) {
                    paths.filter(path -> path.getFileName().toString().matches(pattern))
                         .forEach(path -> matchingFiles.add(createFileInfo(path)));
                }
            } else {
                // Search recursively
                logger.debug("Searching recursively: {}", dirPath);
                try (Stream<Path> paths = Files.walk(dirPath)) {
                    paths.filter(path -> path.getFileName().toString().matches(pattern))
                         .forEach(path -> matchingFiles.add(createFileInfo(path)));
                }
            }
            
            logger.debug("Found {} matches in directory: {}", matchingFiles.size(), dirPath);
            return new SimpleFileResult(true, "Search completed successfully", Map.of("files", matchingFiles));
        } catch (IOException e) {
            logger.error("Error searching directory: {}", directory, e);
            return new SimpleFileResult(false, "Failed to search directory", e.getMessage());
        }
    }
    
    /**
     * Gets a read-write lock for the specified file path.
     *
     * @param path The file path
     * @return A read-write lock for the path
     */
    private ReadWriteLock getFileLock(Path path) {
        return fileLocks.computeIfAbsent(path, k -> new ReentrantReadWriteLock());
    }
    
    /**
     * Checks the cache for a file.
     *
     * @param path The file path
     * @return The cached content, or null if not in cache or if timestamp is stale
     */
    private byte[] checkCache(Path path) {
        if (!fileCache.containsKey(path)) {
            return null;
        }
        
        // Check if file has been modified since it was cached
        try {
            Instant lastModified = Files.getLastModifiedTime(path).toInstant();
            Instant cachedTime = cacheTimestamps.get(path);
            
            if (cachedTime != null && lastModified.isAfter(cachedTime)) {
                // File was modified, invalidate cache
                logger.debug("Cache invalidated due to file modification: {}", path);
                removeFromCache(path);
                return null;
            }
            
            return fileCache.get(path);
        } catch (IOException e) {
            // If there's an error, remove from cache to be safe
            logger.warn("Error checking file timestamp, removing from cache: {}", path, e);
            removeFromCache(path);
            return null;
        }
    }
    
    /**
     * Updates the cache with file content.
     *
     * @param path The file path
     * @param content The file content
     */
    private void updateCache(Path path, byte[] content) {
        // Check if cache is full before adding
        if (fileCache.size() >= maxCachedFiles && !fileCache.containsKey(path)) {
            // Evict the oldest entry
            evictOldestCacheEntry();
        }
        
        fileCache.put(path, content);
        cacheTimestamps.put(path, Instant.now());
        logger.debug("Updated cache for file: {}", path);
    }
    
    /**
     * Removes a file from the cache.
     *
     * @param path The file path
     */
    private void removeFromCache(Path path) {
        fileCache.remove(path);
        cacheTimestamps.remove(path);
        logger.debug("Removed from cache: {}", path);
    }
    
    /**
     * Evicts the oldest entry from the cache based on access time.
     */
    private void evictOldestCacheEntry() {
        if (cacheTimestamps.isEmpty()) {
            return;
        }
        
        Path oldestPath = null;
        Instant oldestTime = Instant.MAX;
        
        for (Map.Entry<Path, Instant> entry : cacheTimestamps.entrySet()) {
            if (entry.getValue().isBefore(oldestTime)) {
                oldestTime = entry.getValue();
                oldestPath = entry.getKey();
            }
        }
        
        if (oldestPath != null) {
            logger.debug("Evicting oldest cache entry: {}", oldestPath);
            removeFromCache(oldestPath);
        }
    }
    
    /**
     * Creates a FileInfo object from a Path.
     * 
     * @param path The path to create a FileInfo from
     * @return A FileInfo object
     */
    private FileInfo createFileInfo(Path path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            
            return FileInfo.create(
                    path.toString(),
                    Files.isDirectory(path),
                    Files.isRegularFile(path),
                    Files.isHidden(path),
                    attrs.size(),
                    attrs.creationTime().toInstant(),
                    attrs.lastModifiedTime().toInstant(),
                    attrs.lastAccessTime().toInstant(),
                    Map.of()
            );
        } catch (IOException e) {
            logger.warn("Error reading file attributes, returning default FileInfo: {}", path, e);
            
            // Return default FileInfo with minimal information
            return FileInfo.create(
                    path.toString(),
                    Files.isDirectory(path),
                    Files.isRegularFile(path),
                    false,
                    -1,
                    Instant.EPOCH,
                    Instant.EPOCH,
                    Instant.EPOCH,
                    Map.of()
            );
        }
    }
    
    /**
     * Gets performance metrics for this adapter.
     *
     * @return A map containing performance metrics
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalReads", totalReads.get());
        metrics.put("totalWrites", totalWrites.get());
        metrics.put("cacheHits", cacheHits.get());
        metrics.put("cacheMisses", cacheMisses.get());
        metrics.put("cacheHitRatio", totalReads.get() > 0 ? 
                (double) cacheHits.get() / totalReads.get() : 0.0);
        metrics.put("asyncOperations", asyncOperations.get());
        metrics.put("dirOperations", dirOperations.get());
        metrics.put("totalBytesRead", totalBytesRead.get());
        metrics.put("totalBytesWritten", totalBytesWritten.get());
        metrics.put("cachedFiles", fileCache.size());
        metrics.put("maxCachedFiles", maxCachedFiles);
        metrics.put("bufferSize", bufferSize);
        
        return metrics;
    }
    
    /**
     * Resets the performance metrics.
     */
    public void resetPerformanceMetrics() {
        totalReads.set(0);
        totalWrites.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        asyncOperations.set(0);
        dirOperations.set(0);
        totalBytesRead.set(0);
        totalBytesWritten.set(0);
        
        logger.info("Performance metrics reset");
    }
    
    /**
     * Clears the file cache.
     */
    public void clearCache() {
        fileCache.clear();
        cacheTimestamps.clear();
        logger.info("File cache cleared");
    }
    
    /**
     * Gets the current number of cached files.
     *
     * @return The number of cached files
     */
    public int getCachedFileCount() {
        return fileCache.size();
    }
}