package org.samstraumr.core;

import org.samstraumr.core.Environment;
import org.samstraumr.core.Tube;

public class TestHelper {
    private static final String DEFAULT_COMPOSITE_ID = "testComposite";
    private static final String DEFAULT_MACHINE_ID = "testMachine";

    public static Tube createTube(String reason, Environment environment) {
        return new Tube(reason, environment, DEFAULT_COMPOSITE_ID, DEFAULT_MACHINE_ID);
    }

    public static Tube createTube(String reason, Environment environment, Tube parentTube) {
        return new Tube(reason, environment, parentTube, DEFAULT_COMPOSITE_ID, DEFAULT_MACHINE_ID);
    }
}
