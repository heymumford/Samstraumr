package org.samstraumr.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TubeConnection {
    private final TubeLogger tubeLogger;
    private final Map<String, String> connectedTubes; // Tube ID to Reason

    public TubeConnection(TubeLogger tubeLogger) {
        this.tubeLogger = tubeLogger;
        this.connectedTubes = new HashMap<>();
    }

    public void connectTo(Tube otherTube) {
        connectedTubes.put(otherTube.getUniqueId(), otherTube.getReason());
        tubeLogger.log("info", "Connected to Tube: " + otherTube.getUniqueId(), "connection");
    }

    public Map<String, String> getConnectedTubes() {
        return Collections.unmodifiableMap(connectedTubes);
    }
}
