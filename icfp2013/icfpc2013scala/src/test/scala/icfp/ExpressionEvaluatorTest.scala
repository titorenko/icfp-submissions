package icfp

import spire.syntax.literals.radix._
import org.junit.Test
import org.junit.Assert._
import spire.math.ULong
import icfp.parser.BVParsers

class ExpressionEvaluatorTest {

  @Test
  def testSimpleShrEval() {
    val e = new BVParsers().parseExpression("(shr1 x)")
    assertEquals(ulong("0"), e.eval(Context("1")))
    assertEquals(ulong("1"), e.eval(Context("2")))
    assertEquals(ulong("2"), e.eval(Context("4")))
  }

  @Test
  def testBoundaryShrEval() {
    val e = new BVParsers().parseExpression("(shr1 x)")
    assertEquals(ulong("7FFFFFFFFFFFFFFF"), e.eval(Context("FFFFFFFFFFFFFFFF")))
  }

  @Test
  def testRegression() {
    val e = new BVParsers().parseExpression("(shl1 x)")
    assertEquals(ulong("5555555554000154"), e.eval(Context("AAAAAAAAAA0000AA")))
  }

  @Test
  def testXor() {
    val e = new BVParsers().parseExpression("(xor 1 x)")
    assertEquals(ulong("0"), e.eval(Context("1")))
    assertEquals(ulong("1"), e.eval(Context("0")))
  }

  @Test
  def testOr() {
    val e = new BVParsers().parseExpression("(or 0 x)")
    assertEquals(ulong("1"), e.eval(Context("1")))
    assertEquals(ulong("0"), e.eval(Context("0")))
  }

  @Test
  def testEvalProblem2() {
    val e = new BVParsers().parseExpression(
      "(shr16 (shr4 (and (xor (shl1 (and 0 (if0 (shr16 (xor (and (xor x x) 0) x)) x 0))) x) x)))");
    assertEquals(ulong("FFE"), e.eval(Context("FFEFEEAA")))
  }

  @Test
  def testEvalSimpleFold1() {
    val e = new BVParsers().parseExpression(
      "(fold x 0 (lambda (y a) (plus y a)))");
    assertEquals(ulong("2"), e.eval(Context("0101")));
  }

  @Test
  def testEvalSimpleFold2() {
    val e = new BVParsers().parseExpression(
      "(fold x 0 (lambda (y a) (plus 1 a)))");
    assertEquals(ulong("8"), e.eval(Context("0101")));
  }

  @Test
  def testEvalFold() {
    val e = new BVParsers().parseExpression(
      "(fold x 0 (lambda (y a) (plus a 1)))");
    assertEquals(ulong("8"), e.eval(Context("fffffffaaa00")));
  }

  @Test
  def testEvalNot0() {
    val e = new BVParsers().parseExpression("(not x)")
    assertEquals(ulong("FFFF FFFF FFFF FFFF"), e.eval(Context("0")));
  }
  
  @Test
  def testToString() {
    assertEquals("FFFFFFFFFFFFFFFF".toLowerCase(), Number(ulong("FFFF FFFF FFFF FFFF")).toString)
  }
}