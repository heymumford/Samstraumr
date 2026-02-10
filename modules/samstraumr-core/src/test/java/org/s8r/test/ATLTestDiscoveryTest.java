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

package org.s8r.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.test.annotation.ATL;

/**
 * Test class to verify the ATL annotation discovery is working correctly.
 *
 * <p>This test is deliberately annotated with @ATL to ensure it gets discovered and executed by the
 * RunATLTests runner. It serves as a verification that the ATL test discovery mechanism is working
 * properly.
 */
@ATL
@Tag("ATL") // Explicit tag to ensure test is discovered
public class ATLTestDiscoveryTest {

  @Test
  @DisplayName("Verifies that ATL annotation discovery is working")
  public void testATLDiscovery() {
    System.out.println("ATL test discovery verification is running!");
    assertTrue(true, "ATL test discovery works");
  }
}
