package org.samstraumr.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Tube class.
 */
public class TubeTest {

    private MockQueenTube mockQueen;
    private TestTube testTube;

    @BeforeEach
    public void setup() {
        mockQueen = new MockQueenTube("Mock Queen");
        testTube = new TestTube("Test Purpose", mockQueen, "MotherTube", Tube.TubeType.VIRTUE_TUBE);
    }

    @Test
    public void testTubeInstantiation() {
        assertNotNull(testTube.getTubeID());
        assertEquals("Test Purpose", testTube.getPurpose());
        assertEquals(Tube.TubeType.VIRTUE_TUBE, testTube.getTubeType());
    }

    @Test
    public void testReceiveInput() {
        testTube.receive("Test Input");
        assertEquals(TubeStatus.ACTIVE, testTube.getStatus());
        assertEquals("Test Input", testTube.getInput().get());
    }

    @Test
    public void testPingQueenWhenInputMissing() {
        testTube.receive(null);
        assertEquals(TubeStatus.WAITING_FOR_INPUT, testTube.getStatus());
        assertTrue(mockQueen.isPinged());
    }

    @Test
    public void testExecuteTube() {
        TestTube nextTube = new TestTube("Next Purpose", mockQueen, "TestTube", Tube.TubeType.BODY_TUBE);
        testTube.receive("Test Input");
        testTube.execute(nextTube);
        assertEquals(TubeStatus.ACTIVE, nextTube.getStatus());
    }
}

// Mock implementation of QueenTube for testing purposes
class MockQueenTube extends QueenTube {
    private boolean pinged = false;

    public MockQueenTube(String purpose) {
        super(purpose);
    }

    @Override
    public void receivePing(Tube<?, ?> tube) {
        this.pinged = true;
    }

    public boolean isPinged() {
        return pinged;
    }
}

// TestTube class for testing purposes
class TestTube extends Tube<String, String> {

    public TestTube(String purpose, QueenTube queenTube, String motherTubeID, TubeType tubeType) {
        super(purpose, queenTube, motherTubeID, tubeType);
    }

    @Override
    protected String process() {
        return getInput().orElse(null);
    }
}

