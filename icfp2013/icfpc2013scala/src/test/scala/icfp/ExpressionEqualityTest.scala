package icfp

import org.junit.Test
import org.junit.Assert._
import icfp.parser.BVParsers

class ExpressionEqualityTest {
 
  @Test
  def testEquality() {
    val e1 = new BVParsers().parseExpression("(shr4 (shr1 x))")
    val e2 = new BVParsers().parseExpression("(shr1 (shr4 x))")
    assertFalse(e1.equals(e2))
    assertEquals(e1.simplify, e2.simplify)
  }
}