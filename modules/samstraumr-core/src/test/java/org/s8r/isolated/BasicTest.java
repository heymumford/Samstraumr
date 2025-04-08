package org.s8r.isolated;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** A basic test class with minimal dependencies to verify that tests can run. */
public class BasicTest {

  @Test
  public void testAddition() {
    assertEquals(4, 2 + 2, "2 + 2 should equal 4");
  }

  @Test
  public void testStringConcatenation() {
    String result = "Hello" + " " + "World";
    assertEquals("Hello World", result, "String concatenation should work");
  }

  @Test
  public void testBooleanLogic() {
    assertTrue(true, "True should be true");
    assertFalse(false, "False should be false");
  }
}
