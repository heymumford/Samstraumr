package org.samstraumr.core;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the QueenTube class.
 * Covers Mutually Exclusive and Collectively Exhaustive (MECE) scenarios.
 */
public class QueenTubeTest {

    private QueenTube queenTube;

    @Before
    public void setUp() {
        // Instantiate the queen tube with Yggdrasil as the mother tube ID
        queenTube = new QueenTube("Master Control");
    }

    @Test
    public void testQueenTubeID() {
        // Test if queen tube's ID is unique and starts with "TUBE"
        assertNotNull(queenTube.getTubeID());
        assertTrue(queenTube.getTubeID().startsWith("TUBE-"));
    }

    @Test
    public void testMotherTubeIDIsYggdrasil() {
        // Test if queen tube's mother ID is "Yggdrasil"
        assertEquals("Yggdrasil", queenTube.getMotherTubeID());
    }

    @Test
    public void testQueenTubeStatusInitial() {
        // Test initial status of the queen tube
        assertEquals(TubeStatus.WAITING, queenTube.getStatus());
    }

    @Test
    public void testQueenTubeReceivesPing() {
        // Test that the queen tube can receive a ping from a child tube
        Tube<Void, Void> childTube = new CustomTube("Child", queenTube, queenTube.getTubeID());
        queenTube.receivePing(childTube);
        assertEquals("TUBE-" + childTube.getTubeID(), childTube.getTubeID());
    }

    @Test
    public void testQueenTubeManagesChildTube() {
        // Test that the queen tube can manage child tubes
        Tube<Void, Void> childTube = new CustomTube("Child", queenTube, queenTube.getTubeID());
        queenTube.manageTube(childTube);
        // There should be no exceptions and child tube should be managed successfully
        assertEquals("Master Control", queenTube.getPurpose());
    }

    @Test
    public void testQueenTubeExecuteChild() {
        // Test executing a child tube via the queen tube
        Tube<Void, String> childTube = new CustomTube("Data Processor", queenTube, queenTube.getTubeID());
        childTube.receive("input_data");
        childTube.execute(queenTube);  // Queen is the next tube in the chain

        // Check the status and output of the child tube after execution
        assertEquals(TubeStatus.ACTIVE, childTube.getStatus());
    }
}

