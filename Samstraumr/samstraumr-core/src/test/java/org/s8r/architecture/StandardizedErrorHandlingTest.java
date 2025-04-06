package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.component.exception.ComponentException;
import org.s8r.component.exception.InvalidStateTransitionException;
import org.s8r.domain.exception.ComponentInitializationException;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.component.composite.CompositeException;
import org.s8r.core.exception.InitializationException;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Standardized Error Handling Strategy as described in ADR-0011.
 * 
 * <p>This test suite validates the implementation of error classification, exception hierarchy,
 * design guidelines, handling responsibilities, recovery patterns, and logging/monitoring.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Standardized Error Handling Tests (ADR-0011)")
public class StandardizedErrorHandlingTest {

    private ErrorLogger mockLogger;

    @BeforeEach
    void setUp() {
        mockLogger = mock(ErrorLogger.class);
    }

    @Nested
    @DisplayName("Error Classification Tests")
    class ErrorClassificationTests {
        
        @Test
        @DisplayName("Recoverable errors should be identifiable")
        void recoverableErrorsShouldBeIdentifiable() {
            // Create different types of exceptions
            ComponentNotFoundException recoverable = new ComponentNotFoundException("Component not found");
            DuplicateComponentException nonRecoverable = new DuplicateComponentException("Duplicate component");
            
            // Check if they're correctly classified
            assertTrue(recoverable.isRecoverable(), "ComponentNotFoundException should be recoverable");
            assertFalse(nonRecoverable.isRecoverable(), "DuplicateComponentException should not be recoverable");
        }
        
        @Test
        @DisplayName("Partial failures should be identifiable")
        void partialFailuresShouldBeIdentifiable() {
            // Create composite exception (partial failure)
            CompositeException compositeEx = new CompositeException("Composite operation partially failed");
            compositeEx.addSuppressed(new ComponentNotFoundException("First component not found"));
            compositeEx.addSuppressed(new ComponentNotFoundException("Second component not found"));
            
            // Create non-partial failure
            ComponentException simpleEx = new ComponentException("Simple component failure");
            
            // Check classification
            assertTrue(compositeEx.isPartialFailure(), "CompositeException should be a partial failure");
            assertFalse(simpleEx.isPartialFailure(), "Simple ComponentException should not be a partial failure");
            assertEquals(2, compositeEx.getFailureCount(), "CompositeException should track number of failures");
        }
        
        @Test
        @DisplayName("Critical failures should be identifiable")
        void criticalFailuresShouldBeIdentifiable() {
            // Create a critical failure
            ComponentInitializationException criticalEx = 
                new ComponentInitializationException("Critical initialization failure");
            
            // Create a non-critical error
            InvalidOperationException nonCriticalEx = 
                new InvalidOperationException("Invalid operation");
            
            // Check classification
            assertTrue(criticalEx.isCritical(), "InitializationException should be critical");
            assertFalse(nonCriticalEx.isCritical(), "InvalidOperationException should not be critical");
        }
    }

    @Nested
    @DisplayName("Exception Hierarchy Tests")
    class ExceptionHierarchyTests {
        
        @Test
        @DisplayName("Exception hierarchy should follow ADR structure")
        void exceptionHierarchyShouldFollowAdrStructure() {
            // Samstraumr base exception
            ComponentException baseEx = new ComponentException("Base exception");
            
            // Domain-specific exceptions
            ComponentInitializationException initEx = 
                new ComponentInitializationException("Initialization failed");
            ComponentNotFoundException notFoundEx = 
                new ComponentNotFoundException("Component not found");
            InvalidStateTransitionException stateEx = 
                new InvalidStateTransitionException("READY", "initialize", "Already initialized");
            
            // Check inheritance relationships
            assertTrue(initEx instanceof ComponentException, 
                "ComponentInitializationException should extend ComponentException");
            assertTrue(notFoundEx instanceof ComponentException, 
                "ComponentNotFoundException should extend ComponentException");
            assertTrue(stateEx instanceof ComponentException, 
                "InvalidStateTransitionException should extend ComponentException");
            
            // Core framework exception
            InitializationException coreInitEx = 
                new InitializationException("Core initialization failed");
            
            // Check relationship to root exception
            assertFalse(coreInitEx instanceof ComponentException, 
                "Core exceptions should be separate from domain exceptions");
        }
    }

    @Nested
    @DisplayName("Exception Design Guidelines Tests")
    class ExceptionDesignGuidelinesTests {
        
        @Test
        @DisplayName("Exceptions should be unchecked")
        void exceptionsShouldBeUnchecked() {
            // Verify that domain exceptions extend RuntimeException
            ComponentException ex = new ComponentException("Test");
            assertTrue(ex instanceof RuntimeException, 
                "Domain exceptions should extend RuntimeException");
        }
        
        @Test
        @DisplayName("Exceptions should include rich context")
        void exceptionsShouldIncludeRichContext() {
            // Create an exception with context
            InvalidStateTransitionException ex = 
                new InvalidStateTransitionException("CREATED", "processData", "Component not initialized");
            
            // Verify context information
            assertEquals("CREATED", ex.getCurrentState(), "Exception should include current state");
            assertEquals("processData", ex.getOperation(), "Exception should include operation");
            
            // Verify message contains context
            String message = ex.getMessage();
            assertTrue(message.contains("CREATED"), "Message should include current state");
            assertTrue(message.contains("processData"), "Message should include operation");
        }
        
        @Test
        @DisplayName("Exceptions should preserve root causes")
        void exceptionsShouldPreserveRootCauses() {
            // Create a chain of exceptions
            IllegalArgumentException rootCause = new IllegalArgumentException("Invalid argument");
            ComponentInitializationException wrapped = 
                new ComponentInitializationException("Initialization failed", rootCause);
            
            // Check that root cause is preserved
            Throwable extracted = wrapped.getCause();
            assertSame(rootCause, extracted, "Root cause should be preserved");
        }
        
        @Test
        @DisplayName("Exceptions should have error codes")
        void exceptionsShouldHaveErrorCodes() {
            // Create exceptions and check their error codes
            ComponentNotFoundException notFoundEx = new ComponentNotFoundException("Not found");
            DuplicateComponentException duplicateEx = new DuplicateComponentException("Duplicate");
            
            // Verify error codes
            assertNotNull(notFoundEx.getErrorCode(), "Exception should have error code");
            assertNotNull(duplicateEx.getErrorCode(), "Exception should have error code");
            assertNotEquals(
                notFoundEx.getErrorCode(), 
                duplicateEx.getErrorCode(), 
                "Different exception types should have different error codes"
            );
        }
    }

    @Nested
    @DisplayName("Error Handling Responsibility Tests")
    class ErrorHandlingResponsibilityTests {
        
        @Test
        @DisplayName("Domain layer should validate domain invariants")
        void domainLayerShouldValidateDomainInvariants() {
            // Create a domain entity (CustomerOrder)
            CustomerOrder order = new CustomerOrder();
            
            // Attempt to add invalid items to order
            assertThrows(IllegalArgumentException.class, () -> {
                order.addItem(null, 1); // Domain validation should reject null
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                order.addItem("ITEM-001", -5); // Domain validation should reject negative quantity
            });
        }
        
        @Test
        @DisplayName("Application layer should handle business rule errors")
        void applicationLayerShouldHandleBusinessRuleErrors() {
            // Create mock repository and service
            OrderRepository mockRepository = mock(OrderRepository.class);
            InventoryService mockInventory = mock(InventoryService.class);
            
            // Set up inventory to throw exception
            doThrow(new OutOfStockException("ITEM-001"))
                .when(mockInventory).checkAvailability("ITEM-001", 10);
            
            // Create order service that uses both dependencies
            OrderService orderService = new OrderService(mockRepository, mockInventory);
            
            // Test that business rule is enforced at application layer
            assertThrows(BusinessRuleViolationException.class, () -> {
                orderService.placeOrder("CUST-001", Map.of("ITEM-001", 10));
            });
        }
        
        @Test
        @DisplayName("Infrastructure layer should handle resource errors")
        void infrastructureLayerShouldHandleResourceErrors() {
            // Create mock database connection
            DatabaseConnection mockDb = mock(DatabaseConnection.class);
            doThrow(new DatabaseException("Connection failed"))
                .when(mockDb).execute(anyString());
            
            // Create repository with this connection
            OrderRepositoryImpl repository = new OrderRepositoryImpl(mockDb);
            
            // Attempt to save order
            CustomerOrder order = new CustomerOrder();
            
            // Infrastructure layer should translate technical exception to domain exception
            Exception ex = assertThrows(RepositoryException.class, () -> {
                repository.save(order);
            });
            
            assertTrue(ex.getCause() instanceof DatabaseException, 
                "Original technical exception should be preserved as cause");
        }
    }

    @Nested
    @DisplayName("Error Recovery Patterns Tests")
    class ErrorRecoveryPatternsTests {
        
        @Test
        @DisplayName("Retry pattern should handle transient failures")
        void retryPatternShouldHandleTransientFailures() {
            // Create service with retry capability
            RetryingOrderService service = new RetryingOrderService();
            
            // First two attempts fail, third succeeds
            service.setFailCount(2);
            
            // Execute with retry
            CustomerOrder result = service.getOrder("ORDER-001");
            
            // Verify retries happened
            assertEquals(3, service.getAttemptCount(), "Should have tried 3 times");
            assertNotNull(result, "Should eventually succeed");
        }
        
        @Test
        @DisplayName("Circuit breaker should prevent cascading failures")
        void circuitBreakerShouldPreventCascadingFailures() {
            // Create service with circuit breaker
            CircuitBreakerService service = new CircuitBreakerService();
            
            // Make service fail consistently
            service.setAlwaysFail(true);
            
            // Initial calls should try and fail
            for (int i = 0; i < CircuitBreakerService.FAILURE_THRESHOLD; i++) {
                assertThrows(ServiceException.class, () -> service.callExternalService());
            }
            
            // Circuit should now be open
            assertTrue(service.isCircuitOpen(), "Circuit should be open after threshold failures");
            
            // Further calls should fail fast without trying
            long start = System.nanoTime();
            assertThrows(CircuitOpenException.class, () -> service.callExternalService());
            long duration = System.nanoTime() - start;
            
            assertTrue(duration < 1_000_000, // less than 1ms
                "Call with open circuit should fail immediately");
        }
        
        @Test
        @DisplayName("Fallback mechanism should provide default behavior")
        void fallbackMechanismShouldProvideDefaultBehavior() {
            // Create service with fallback
            FallbackService service = new FallbackService();
            
            // Make primary method fail
            service.setPrimaryFails(true);
            
            // Call should succeed with fallback value
            CustomerOrder result = service.getOrder("ORDER-001");
            
            assertNotNull(result, "Should return fallback result");
            assertTrue(result.isFallback(), "Should be marked as fallback result");
        }
    }

    @Nested
    @DisplayName("Error Logging and Monitoring Tests")
    class ErrorLoggingAndMonitoringTests {
        
        @Test
        @DisplayName("Errors should be logged in structured format")
        void errorsShouldBeLoggedInStructuredFormat() {
            // Create error handler that uses logger
            ErrorHandler handler = new ErrorHandler(mockLogger);
            
            // Handle different errors
            ComponentNotFoundException notFoundEx = 
                new ComponentNotFoundException("Component XYZ not found");
            handler.handleError(notFoundEx);
            
            // Verify structured logging
            verify(mockLogger).logStructured(
                eq("ERROR"),
                eq("ComponentNotFoundException"),
                eq(notFoundEx.getErrorCode()),
                anyMap()
            );
        }
        
        @Test
        @DisplayName("Correlation IDs should be preserved in logs")
        void correlationIdsShouldBePreservedInLogs() {
            // Create error with correlation ID
            String correlationId = UUID.randomUUID().toString();
            ComponentException ex = new ComponentException("Test error");
            ex.setCorrelationId(correlationId);
            
            // Create handler and process error
            ErrorHandler handler = new ErrorHandler(mockLogger);
            handler.handleError(ex);
            
            // Verify correlation ID is logged
            verify(mockLogger).logStructured(
                anyString(),
                anyString(),
                anyString(),
                argThat(map -> correlationId.equals(map.get("correlationId")))
            );
        }
    }
    
    // Mock interfaces and classes for tests
    
    interface ErrorLogger {
        void logStructured(String level, String type, String code, Map<String, Object> data);
    }
    
    static class CustomerOrder {
        private final Map<String, Integer> items = new HashMap<>();
        private boolean fallback;
        
        void addItem(String itemId, int quantity) {
            if (itemId == null) {
                throw new IllegalArgumentException("Item ID cannot be null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            items.put(itemId, quantity);
        }
        
        void setFallback(boolean fallback) {
            this.fallback = fallback;
        }
        
        boolean isFallback() {
            return fallback;
        }
    }
    
    interface OrderRepository {
        void save(CustomerOrder order) throws RepositoryException;
        CustomerOrder findById(String orderId) throws RepositoryException;
    }
    
    interface InventoryService {
        void checkAvailability(String itemId, int quantity) throws OutOfStockException;
    }
    
    interface DatabaseConnection {
        void execute(String sql) throws DatabaseException;
    }
    
    static class OrderService {
        private final OrderRepository repository;
        private final InventoryService inventory;
        
        OrderService(OrderRepository repository, InventoryService inventory) {
            this.repository = repository;
            this.inventory = inventory;
        }
        
        void placeOrder(String customerId, Map<String, Integer> items) 
                throws BusinessRuleViolationException {
            try {
                // Check inventory for all items
                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                    inventory.checkAvailability(entry.getKey(), entry.getValue());
                }
                
                // Create and save order
                CustomerOrder order = new CustomerOrder();
                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                    order.addItem(entry.getKey(), entry.getValue());
                }
                repository.save(order);
            } catch (OutOfStockException e) {
                throw new BusinessRuleViolationException("Cannot place order with out-of-stock items", e);
            } catch (RepositoryException e) {
                throw new BusinessRuleViolationException("Order could not be saved", e);
            }
        }
    }
    
    static class OrderRepositoryImpl implements OrderRepository {
        private final DatabaseConnection db;
        
        OrderRepositoryImpl(DatabaseConnection db) {
            this.db = db;
        }
        
        @Override
        public void save(CustomerOrder order) throws RepositoryException {
            try {
                db.execute("INSERT INTO orders ...");
            } catch (DatabaseException e) {
                throw new RepositoryException("Failed to save order", e);
            }
        }
        
        @Override
        public CustomerOrder findById(String orderId) throws RepositoryException {
            try {
                db.execute("SELECT * FROM orders WHERE id = " + orderId);
                return new CustomerOrder();
            } catch (DatabaseException e) {
                throw new RepositoryException("Failed to find order: " + orderId, e);
            }
        }
    }
    
    static class RetryingOrderService {
        private int failCount;
        private int attemptCount;
        
        void setFailCount(int failCount) {
            this.failCount = failCount;
        }
        
        int getAttemptCount() {
            return attemptCount;
        }
        
        CustomerOrder getOrder(String orderId) {
            attemptCount = 0;
            
            while (true) {
                try {
                    attemptCount++;
                    if (attemptCount <= failCount) {
                        throw new TransientException("Temporary error");
                    }
                    return new CustomerOrder();
                } catch (TransientException e) {
                    if (attemptCount >= 3) {
                        throw new ServiceException("Failed after retries", e);
                    }
                    try {
                        Thread.sleep(10); // Short delay in test
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new ServiceException("Interrupted during retry", ex);
                    }
                }
            }
        }
    }
    
    static class CircuitBreakerService {
        static final int FAILURE_THRESHOLD = 3;
        private boolean alwaysFail;
        private int failureCount;
        private boolean circuitOpen;
        
        void setAlwaysFail(boolean alwaysFail) {
            this.alwaysFail = alwaysFail;
        }
        
        boolean isCircuitOpen() {
            return circuitOpen;
        }
        
        String callExternalService() {
            if (circuitOpen) {
                throw new CircuitOpenException("Circuit is open");
            }
            
            try {
                if (alwaysFail) {
                    throw new ServiceException("Service call failed");
                }
                failureCount = 0; // Reset on success
                return "SUCCESS";
            } catch (ServiceException e) {
                failureCount++;
                if (failureCount >= FAILURE_THRESHOLD) {
                    circuitOpen = true;
                }
                throw e;
            }
        }
    }
    
    static class FallbackService {
        private boolean primaryFails;
        
        void setPrimaryFails(boolean primaryFails) {
            this.primaryFails = primaryFails;
        }
        
        CustomerOrder getOrder(String orderId) {
            try {
                if (primaryFails) {
                    throw new ServiceException("Primary method failed");
                }
                return new CustomerOrder();
            } catch (Exception e) {
                return getFallbackOrder(orderId);
            }
        }
        
        private CustomerOrder getFallbackOrder(String orderId) {
            CustomerOrder fallback = new CustomerOrder();
            fallback.setFallback(true);
            return fallback;
        }
    }
    
    static class ErrorHandler {
        private final ErrorLogger logger;
        
        ErrorHandler(ErrorLogger logger) {
            this.logger = logger;
        }
        
        void handleError(Exception ex) {
            String type = ex.getClass().getSimpleName();
            String code = "UNKNOWN";
            
            if (ex instanceof ComponentException) {
                code = ((ComponentException) ex).getErrorCode();
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("message", ex.getMessage());
            
            if (ex instanceof ComponentException && 
                    ((ComponentException) ex).getCorrelationId() != null) {
                data.put("correlationId", ((ComponentException) ex).getCorrelationId());
            }
            
            logger.logStructured("ERROR", type, code, data);
        }
    }
    
    // Mock exception classes
    
    static class OutOfStockException extends Exception {
        OutOfStockException(String message) {
            super(message);
        }
    }
    
    static class BusinessRuleViolationException extends Exception {
        BusinessRuleViolationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    static class RepositoryException extends Exception {
        RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    static class DatabaseException extends Exception {
        DatabaseException(String message) {
            super(message);
        }
    }
    
    static class TransientException extends RuntimeException {
        TransientException(String message) {
            super(message);
        }
    }
    
    static class ServiceException extends RuntimeException {
        ServiceException(String message) {
            super(message);
        }
        
        ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    static class CircuitOpenException extends RuntimeException {
        CircuitOpenException(String message) {
            super(message);
        }
    }
}