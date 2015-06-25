package problems

import icfp.Operator
import icfp.Expression
import spire.math.ULong

class Problem(val id: String, val ops: Set[Operator], val size: Int, val evals: Vector[(ULong, ULong)], 
  val solution: Option[Expression]=None, val found: Option[Expression] = None) {

  val args = evals.map(_._1)
  val values = evals.map(_._2)
  
  override def toString() = s"Problem($id) of size $size with $ops"
}
