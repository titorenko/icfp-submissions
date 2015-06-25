package solver

import icfp._
import problems.Problem
import problems.TrainProblemDatabase
import problems.Problem

object NaiveSolver extends App {
  val toSolve: Stream[Problem] = TrainProblemDatabase.problems.filter(p => isIncluded(p.ops))
  
  def isIncluded(op: Set[Operator]): Boolean = {
    !(op.contains(Bonus) || op.contains(TFold) || op.contains(Fold))
  }
  
  def solve(p: Problem): Option[Expression] = {
    println(s"Solving $p")
    val start = System.currentTimeMillis
	
    val generator = ExpressionGenerator(p.ops, true)
	val expressions = generator.generate(9, false)
	val result = new NaiveMemorySearcher(expressions, p).find
	
	println("Finished solving in "+ (System.currentTimeMillis - start) + " millis")
	println(s"Solution: $result\n")
	result
  }
  
  
  val problems = toSolve.take(1000)
  println("Solving "+problems.size+" problems")
  val start = System.currentTimeMillis
  problems.foreach(solve(_))
  println("All done "+ (System.currentTimeMillis - start) + " millis")
  
}

class NaiveMemorySearcher(val dictionary: Set[Expression], val p: Problem) {
  def find(): Option[Expression] = {
    dictionary.par.find(hasSameValues(_))
  }
  
  private def hasSameValues(e: Expression): Boolean = e.eval(p.args) == p.values
}