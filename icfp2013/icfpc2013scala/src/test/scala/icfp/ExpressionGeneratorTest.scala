package icfp
import org.junit.Test
import org.junit.Assert._


class ExpressionGeneratorTest {
  @Test
  def testGenerateUnary() {
	val ops: Set[Operator] = Set(Shr(1), Shr(4))
    val es = ExpressionGenerator(ops).generate(3)
    println(es);
    assertEquals(8, es.size) //0, 1, x, shr1x, shr4x, shr2x, shr5x, shr8x
  }
  
  @Test
  def testGenerateBinary() {
	val ops: Set[Operator] = Set(Xor, Not)
	val es = ExpressionGenerator(ops).generate(5)
    println(es)
  }
  
  @Test
  def testGenerateIf0() {
    val ops: Set[Operator] = Set(If0, Shl(1))
    val es = ExpressionGenerator(ops).generate(4)
    println(es)
  }
}