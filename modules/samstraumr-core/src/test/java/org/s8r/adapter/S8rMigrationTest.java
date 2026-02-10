/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.UnitTest;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;

/**
 * Tests for the S8r migration utilities.
 *
 * <p>These tests verify that the migration utilities correctly bridge between the legacy Tube-based
 * system and the new Component-based architecture.
 */
@UnitTest
public class S8rMigrationTest {

  @Test
  public void testTubeToComponentConversion() {
    // Create test objects
    Environment env = new Environment();
    env.setParameter("name", "TestTube");
    env.setParameter("type", "UnitTest");

    Tube tube = Tube.create("Testing migration", env);

    // Create factory and convert
    S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger("Test"));
    ComponentPort componentPort = factory.tubeToComponent(tube);

    // Verify basic conversion
    assertNotNull(componentPort);
    assertEquals(tube.getUniqueId(), componentPort.getId().getIdString());
    assertEquals(LifecycleState.READY, componentPort.getLifecycleState());
  }
}
