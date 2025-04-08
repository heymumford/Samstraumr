package org.s8r.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** A simple test class to verify that tests can run. */
public class SimpleTest {

  @Test
  public void testAddition() {
    assertEquals(4, 2 + 2, "2 + 2 should equal 4");
  }

  @Test
  public void testStringConcat() {
    String result = "Hello" + " " + "World";
    assertEquals("Hello World", result, "String concatenation should work");
  }
}
