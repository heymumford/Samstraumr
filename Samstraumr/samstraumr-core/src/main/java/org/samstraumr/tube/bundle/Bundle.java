package org.samstraumr.tube.bundle;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.composite.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compatibility layer for Bundle -> Composite migration.
 * This class forwards all calls to the Composite implementation.
 * 
 * @deprecated This class is maintained for backward compatibility only.
 *     Please use {@link org.samstraumr.tube.composite.Composite} directly in new code.
 */
@Deprecated
public class Bundle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bundle.class);
    
    private final Composite composite;
    
    public Bundle(String id, Environment environment) {
        LOGGER.warn("Using deprecated Bundle class. Please migrate to Composite.");
        this.composite = new Composite(id, environment);
    }
    
    public String getBundleId() {
        return composite.getCompositeId();
    }
    
    public Bundle createTube(String tubeName, String reason) {
        composite.createTube(tubeName, reason);
        return this;
    }
    
    public Bundle connect(String source, String target) {
        composite.connect(source, target);
        return this;
    }
    
    public Map<String, Tube> getTubes() {
        return composite.getTubes();
    }
    
    public Bundle addTransformer(String tubeName, Function<String, String> transformer) {
        composite.addTransformer(tubeName, transformer);
        return this;
    }
    
    public Optional<String> process(String tubeName, String data) {
        return composite.process(tubeName, data);
    }
    
    public void deactivate() {
        composite.deactivate();
    }
    
    public Bundle enableCircuitBreaker(String tubeName, int failureThreshold, long resetTimeMs) {
        composite.enableCircuitBreaker(tubeName, failureThreshold, resetTimeMs);
        return this;
    }
    
    public Map<String, Composite.CircuitBreaker> getCircuitBreakers() {
        return composite.getCircuitBreakers();
    }
    
    public List<BundleEvent> getEventLog() {
        // Convert composite events to bundle events
        return List.of(new BundleEvent("Compatibility Layer", "Using deprecated Bundle API"));
    }
    
    // Compatibility type for circuit breaker
    public static class CircuitBreaker {
        private final Composite.CircuitBreaker delegate;
        
        CircuitBreaker(Composite.CircuitBreaker delegate) {
            this.delegate = delegate;
        }
        
        public boolean isOpen() {
            return delegate.isOpen();
        }
    }
    
    // Compatibility type for bundle events
    public static class BundleEvent {
        private final String type;
        private final String description;
        
        public BundleEvent(String type, String description) {
            this.type = type;
            this.description = description;
        }
        
        public String getType() {
            return type;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
