package org.s8r.architecture.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Suppressions for specific dependency violations in the architecture tests.
 * <p>
 * This class maintains a list of dependencies that are known to be in violation
 * but are temporarily allowed for practical reasons, such as when migrating
 * legacy code to a cleaner architecture.
 * </p>
 */
public class DependencySuppressions {
    
    /**
     * Set of suppressed package dependencies in the format "sourcePackage -> targetPackage".
     */
    private static final Set<String> SUPPRESSED_DEPENDENCIES = new HashSet<>(Arrays.asList(
        // Adapter dependencies on legacy code
        "org.s8r.adapter -> org.s8r.core.env.Environment",
        "org.s8r.adapter -> org.s8r.core.tube.identity.Identity",
        "org.s8r.adapter -> org.s8r.tube.TubeIdentity",
        "org.s8r.adapter -> org.s8r.tube.Environment"
    ));
    
    /**
     * Checks if a dependency violation should be suppressed.
     *
     * @param sourcePackage the source package
     * @param targetPackage the target package
     * @return true if the dependency violation should be suppressed, false otherwise
     */
    public static boolean isSuppressed(String sourcePackage, String targetPackage) {
        return SUPPRESSED_DEPENDENCIES.contains(sourcePackage + " -> " + targetPackage);
    }
}