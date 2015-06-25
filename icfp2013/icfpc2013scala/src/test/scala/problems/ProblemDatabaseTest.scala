package problems

import org.junit.Test
import org.junit.Assert._
import icfp._
import icfp.parser.BVParsers
import org.junit.Ignore

class ProblemDatabaseTest {
  
  @Test
  def testTrainProblemIds() {
    assertEquals(2167, TrainProblemDatabase.problemsIds.size)
  }
  
  @Test
  def testReadTrainProblem() {
    val id = "01d6LbcuiTbNtFRClhAHoOBC"
    val problem = TrainProblemDatabase.readProblem(id)
    assertEquals(23, problem.size)
    assertEquals(Set(Shr(4), Shl(1), And, Xor, If0, TFold, Not, Or), problem.ops)
    assertEquals(256, problem.evals.size)
    assertEquals(ulong("1"), problem.evals(0)._1)
    assertEquals(ulong("2"), problem.evals(0)._2)
    assertEquals(ulong("47a642653b3a0f21"), problem.evals(255)._1)
    val e = problem.solution.get
    assertEquals("(fold x 0 (lambda (y a) (shl1 (and (xor y (not (shr4 (or (if0 (xor (shl1 (xor y a)) a) y 1) 0)))) 1))))",
        e.toString())
    assertEquals(Set(VarY, VarA), e.asInstanceOf[FoldExpr].foldVars)
  }
  
  @Test
  def testReadRealProblem() {
    val id = "03oCV9VFCSSl9sTbE3NO9y6v"
    val problem = RealProblemDatabase.readProblem(id)
    val e = problem.solution.get
    assertEquals("(fold (shr1 (if0 x x x)) x (lambda (y a) (xor (shr16 a) y)))", e.toString())
    assertEquals(Set(VarX, VarY, VarA), e.asInstanceOf[FoldExpr].foldVars)
    assertEquals("(fold 1 1 (lambda (y a) (shr1 (if0 y a (shr1 (shr16 (shr16 (shr16 x))))))))", 
        problem.found.get.toString())
  }
  
  @Test @Ignore
  def testReadOps() {
    println(TrainProblemDatabase.problemsByOperator.map(_._1))
  }
}
