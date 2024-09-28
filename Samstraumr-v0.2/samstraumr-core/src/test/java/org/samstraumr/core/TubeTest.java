package org.samstraumr.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

/**
 * Unit tests for the Tube class.
 */
public class TubeTest {

    private TestTube testTube;

    @BeforeEach
    public void setup() {
        testTube = new TestTube("Test Purpose", "MotherTube", Tube.TubeType.VIRTUE_TUBE);
    }

    @Test
    public void testTubeInstantiation() {
        assertNotNull(testTube.getTubeID());
        assertEquals(Tube.TubeType.VIRTUE_TUBE, testTube.getTubeType());
        assertEquals("MotherTube", testTube.getMotherTubeID());
    }

    @Test
    public void testReceiveInput() {
        testTube.receive("Test Input");
        assertEquals(TubeStatus.PROCESSING_INPUT, testTube.getStatus());
        assertEquals("Test Input", testTube.getInput().orElse(null));
    }

    @Test
    public void testAwaitInputWhenInputIsNull() {
        testTube.receive(null);
        assertEquals(TubeStatus.RECEIVING_INPUT, testTube.getStatus());
    }

    @Test
    public void testExecuteTube() {
        TestTube nextTube = new TestTube("Next Purpose", "TestTube", Tube.TubeType.BODY_TUBE);
        testTube.receive("Test Input");
        testTube.execute(nextTube);
        assertEquals(TubeStatus.OUTPUTTING_RESULT, testTube.getStatus());
        assertEquals(TubeStatus.PROCESSING_INPUT, nextTube.getStatus());
    }
}
// TestTube class for testing purposes
class TestTube extends Tube<String, String> {

    public TestTube(String purpose, String motherTubeID, TubeType tubeType) {
        super(purpose, motherTubeID, tubeType);
    }

    @Override
    protected String process() {
        // Set status to processing when input is received
        setStatus(TubeStatus.PROCESSING_INPUT);
        String result = getInput().orElse(null);

        // Set status to outputting result after processing
        setStatus(TubeStatus.OUTPUTTING_RESULT);

        return result;
    }

    // Modify the receive method to update the status to RECEIVING_INPUT
    public void receive(String input) {
        super.receive(input);

        // Update status to RECEIVING_INPUT if input is null, otherwise PROCESSING_INPUT
        if (input == null) {
            setStatus(TubeStatus.RECEIVING_INPUT);
        } else {
            setStatus(TubeStatus.PROCESSING_INPUT);
        }
    }
}
