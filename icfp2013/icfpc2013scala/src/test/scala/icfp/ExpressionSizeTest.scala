package icfp

import org.junit.Test
import org.junit.Assert._
import icfp.parser.BVParsers

class ExpressionSizeTest {

  @Test 
  def testSampleExpressionSize() {
    val e = new BVParsers().parseExpression("(shl1 (xor x (shl1 x)))")
    assertEquals(5, e.size)
  }
  @Test
  def testGenerateBinary() {
	val ops: Set[Operator] = Set(Xor, Not, Shr(1), Shl(1), If0, Plus)
	val es = ExpressionGenerator(ops, false).generate(5, true)
	es.foreach(e => assertEquals(5, e.size))
  }
}