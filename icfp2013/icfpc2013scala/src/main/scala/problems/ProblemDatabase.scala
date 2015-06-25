package problems

import icfp._
import scala.io.Source
import java.util.Properties
import spire.math.ULong
import icfp.parser.BVParsers
import icfp.parser.BVParsers
import scala.collection.immutable.TreeMap

object RealProblemDatabase extends ProblemDatabase("/real")

object TrainProblemDatabase extends ProblemDatabase("/train")

private[problems] class ProblemDatabase(root: String) {
  final val suffix = ".txt"
  
  type ProblemId = String
  
  lazy val problemsIds: List[ProblemId] = {
    val problemStream = getClass().getResourceAsStream(root)
    Source.fromInputStream(problemStream).getLines().map(
        id => if (id.endsWith(suffix)) id.substring(0, id.length()-suffix.length()) else id
    ).toList
  }
  
  lazy val problems: Stream[Problem] = problemsIds.toStream.map(pid => readProblem(pid))
  
  lazy val problemsByOperator: Vector[(Set[Operator], List[Problem])]  = {
    val problems = problemsIds.map(pid => readProblem(pid))
    val grouped = problems.groupBy(p => p.ops).toVector
    grouped.sortBy(_._1.size)
  }
    
  def readProblem(pid: String): Problem = {
    val problemStream = getClass().getResourceAsStream(root+"/"+pid+suffix)
    val props = new Properties()
    props.load(problemStream)
    val ops = readListProperty(props.getProperty("ops"), Operator.fromString(_))
    val size = augmentString(props.getProperty("size")).toInt
    val x = readListProperty(props.getProperty("x"), ulong(_))
    val y = readListProperty(props.getProperty("y"), ulong(_))
    val evals = x zip y
    val solution = parseBVExpression(props.getProperty("solution"))
    val found = parseBVExpression(props.getProperty("found"))
    new Problem(pid, ops.toSet, size, evals.toVector, solution, found)
  }
  
  private def readListProperty[T](prop: String, converter: String => T): Seq[T] = {
    prop.replaceAll("\\[|\\]", "").split(",").map(converter(_)).toSeq
  }
  
  
  final val idRegex: String = """\p{Lower}[\p{Lower}\p{Digit}_]*""";
  final val xRegex: String = s"lambda\\s*\\(($idRegex)\\)"
  private def parseBVExpression(s: String): Option[Expression] = {
    if (s == null) None 
    else {
      
      val xmatch = xRegex.r.findFirstMatchIn(s)
      if (xmatch.isDefined) {
	      val xname = xmatch.get.group(1)
	      val yamatch = s"lambda\\s*\\(($idRegex)\\s+($idRegex)\\)".r.findFirstMatchIn(s)
	      val parser = 
	        if (yamatch.isDefined) 
	          new BVParsers(xname, yamatch.get.group(1), yamatch.get.group(2))
	        else
	          new BVParsers(xname)
	      Some(parser.parseProgram(s))
      } else {
        Some(new BVParsers().parseExpression(s))
      }
    }
  }
}