package perf

import org.scalameter.PerformanceTest
import org.scalameter.Gen
import icfp.parser.BVParsers
import spire.math.ULong
import icfp._
import search.SearchEngine
import search.ConstraintStore
import domain.Identifier
import submission.GuessVerifier
import com.google.common.collect.ImmutableSet
import domain.Expression
import search.SearchEngineMode

object ExpressionGenMeter extends PerformanceTest.Quickbenchmark {
  val sops: Set[Operator] = Set(Not, Shr(1), Shr(4), Shr(16), Shl(1), And, Or, Xor, Plus, If0)
  val jops = new ConstraintStore(
    Array("not", "shl1", "shr1", "shr4", "shr16", "xor", "and", "or", "plus", "if0"),
    Array(), Array()) {
    override def isSatisfiable(e: domain.Expression, id: Identifier) = false
  }
  val maxSize = 8
  val sizes = Gen.enumeration("sizes")(4, 5, maxSize)

  performance of "Expression Generation" in {
    measure method "scala" in {
      using(sizes) in { s =>
        ExpressionGenerator(sops).generate(s, true)
      }
    }
    measure method "java" in {
      using(sizes) in { s =>
        measureJava(s)
      }
    }
  }
  val nExpressionsScala = ExpressionGenerator(sops).generate(maxSize, true).size
  val nExpressionsJava = measureJava(maxSize).size
  
  println(s"Scala: $nExpressionsScala, Java: $nExpressionsJava")

  private[this] def measureJava(s: Int):java.util.Set[Expression] = {
    val id = new Identifier("x")
    val engine = new SearchEngine(id, GuessVerifier.NONE, SearchEngineMode.PESSIMISTIC)
    engine.getExpressions(s, ImmutableSet.of(id), jops)
  }
}