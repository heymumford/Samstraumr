package org.samstraumr.tube;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.samstraumr.tube.bundle.Bundle;
import org.samstraumr.tube.composite.Composite;

/** Basic test to verify Tube, Bundle, and Composite functionality. */
public class SimpleTubeTest {

  @Test
  public void testTubeCreation() {
    Environment env = new Environment();
    Tube tube = Tube.create("Test tube creation", env);

    assertNotNull(tube, "Tube should be created");
    assertNotNull(tube.getUniqueId(), "Tube should have a unique ID");
    assertTrue(tube.getUniqueId().length() > 0, "Tube ID should not be empty");
  }

  @Test
  public void testBundleCreation() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("test-bundle", env);

    assertEquals("test-bundle", bundle.getBundleId(), "Bundle should have the correct ID");
    assertTrue(bundle.isActive(), "Bundle should be active");
  }

  @Test
  public void testCompositeCreation() {
    Environment env = new Environment();
    Composite composite = new Composite("test-composite", env);

    assertEquals(
        "test-composite", composite.getCompositeId(), "Composite should have the correct ID");
    assertTrue(composite.isActive(), "Composite should be active");
  }

  @Test
  public void testBundleCompositeCompatibility() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("compat-test", env);

    // Test that Bundle correctly delegates to Composite
    bundle.createTube("source", "Source tube");
    bundle.createTube("sink", "Sink tube");
    bundle.connect("source", "sink");

    assertEquals(2, bundle.getTubes().size(), "Bundle should have 2 tubes");
    assertEquals(1, bundle.getConnections().size(), "Bundle should have 1 connection");
  }
}
