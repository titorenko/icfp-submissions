package icfp
import org.junit.Test
import icfp.parser.BVParsers

class BVParserTest {

  @Test
  def testSimplestProgramParse() {
    val program = "(lambda (x) (shr1 0) )"
    println(new BVParsers().parseProgram(program))
  }

  @Test
  def testFoldProgramParse() {
    val program = "(lambda (x) (fold x 0 (lambda (y a) (xor (shr4 y) a))))"
    println(new BVParsers().parseProgram(program))
  }
  
  @Test
  def testIf0Parse() {
    val program = "(if0 (plus (shr4 (shr1 x)) x) (not 0) 1)"
    println(new BVParsers().parseExpression(program))
  }
}

