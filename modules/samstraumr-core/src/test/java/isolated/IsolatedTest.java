package isolated;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** A completely isolated test class with no dependencies on other project code. */
public class IsolatedTest {

  @Test
  public void testAddition() {
    assertEquals(4, 2 + 2, "2 + 2 should equal 4");
  }
}
