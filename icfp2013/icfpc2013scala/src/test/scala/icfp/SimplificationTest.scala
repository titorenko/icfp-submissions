package icfp

import org.junit.Test
import org.junit.Assert._
import icfp.parser.BVParsers

class SimplificationTest {
  
  @Test
  def testSimplifyShr() {
    val e = new BVParsers().parseExpression("(shr4 (shr1 x))")
    assertEquals("(shr5 x)", e.simplify.get.toString());
  }
  
  @Test
  def testSimplifyNotXor() {
    val e = new BVParsers().parseExpression("(not (xor 1 x))")
    assertEquals("(xor fffffffffffffffe x)", e.simplify.get.toString());
  }
  
  @Test
  def testSimplifyIf0() {
    val e = new BVParsers().parseExpression("(if0 x 1 1)")
    assertEquals(None, e.simplify);
  }
  
  @Test
  def testSimplifyWithMaxValue {
    val e = new BVParsers().parseExpression("(shr16 (and x (plus 1 1)))")
    assertEquals(None, e.simplify);
  }

}