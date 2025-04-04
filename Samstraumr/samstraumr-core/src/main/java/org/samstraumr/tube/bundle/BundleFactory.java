package org.samstraumr.tube.bundle;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.composite.CompositeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compatibility layer for BundleFactory -> CompositeFactory migration.
 * 
 * @deprecated This class is maintained for backward compatibility only.
 *     Please use {@link org.samstraumr.tube.composite.CompositeFactory} directly in new code.
 */
@Deprecated
public class BundleFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BundleFactory.class);
    
    /**
     * Creates a transformation bundle.
     *
     * @param environment the environment for the bundle
     * @return a new transformation bundle
     * @deprecated Use CompositeFactory.createTransformationComposite instead
     */
    @Deprecated
    public static Bundle createTransformationBundle(Environment environment) {
        LOGGER.warn("Using deprecated BundleFactory. Please migrate to CompositeFactory.");
        return new Bundle("transformation-bundle", environment);
    }
    
    /**
     * Creates a monitoring bundle.
     *
     * @param environment the environment for the bundle
     * @return a new monitoring bundle
     * @deprecated Use CompositeFactory.createMonitoringComposite instead
     */
    @Deprecated
    public static Bundle createMonitoringBundle(Environment environment) {
        LOGGER.warn("Using deprecated BundleFactory. Please migrate to CompositeFactory.");
        return new Bundle("monitoring-bundle", environment);
    }
}
