package perf

import org.scalameter.PerformanceTest
import org.scalameter.Gen
import icfp.parser.BVParsers
import spire.math.ULong
import icfp._
import icfp.Expression

object ExpressionEvalMeter extends PerformanceTest.Quickbenchmark {
  
  val e1 = "(fold x 0 (lambda (y a) (plus (if0 a (plus y y) a) (shl1 (not (or (and 1 (shl1 (shr16 (if0 (plus (plus (shl1 0) y) a) a a)))) y))))))"	
  val e2 = "(if0 (and (not (plus x 1)) x) x 1)"
  val e3 = "(xor (shr16 (shr1 x)) (not x))"
  
  val bvexpr = Gen.enumeration("expressions")(e1, e2, e3)
  val sexpr = for ( e <- bvexpr ) yield new BVParsers().parseExpression(e)
  val jexpr = for ( e <- bvexpr) yield domain.Expression.fromString(e);
  
  val xs: Seq[ULong] = List("0", "FF", "FFFF", "FFFF FF", "FFFF FFFF", "FFFF FFFF FFFF", "FFFF FFFF FFFF FFFF", 
      "AAAA", "AAAA 5555", "AAAA 5555 AAAA 5555")
  val sctxs = xs.map(x => Context(x))
  val jctxs = xs.map(x => domain.Context.contextForPartialEvaluation(x.toBigInt.bigInteger))
  
  performance of "Expression Evaluation" in {
    measure method "scala" in {
      using(sexpr) in {
        e => sctxs.map(c => e.eval(c)) 
      }
    }
    
    measure method "java" in {
      using(jexpr) in {
        e => jctxs.map(c => e.evaluate(c)) 
      }
    }
  }
}