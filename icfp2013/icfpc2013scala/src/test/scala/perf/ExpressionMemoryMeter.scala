package perf

import org.scalameter.PerformanceTest
import org.scalameter.Executor
import org.scalameter.reporting.LoggingReporter
import org.scalameter.Persistor
import org.scalameter.execution.LocalExecutor
import org.scalameter.Aggregator
import org.scalameter.execution.SeparateJvmsExecutor
import org.scalameter.Gen
import icfp.ExpressionGenerator
import icfp.Shr
import icfp.Xor
import icfp.If0
import icfp.Shl
import icfp.Not
import icfp.Operator
import icfp.And
import icfp.Plus
import icfp.Or
import search.ConstraintStore
import domain.Identifier
import domain.Expression
import search.SearchEngine
import submission.GuessVerifier
import search.SearchEngineMode
import com.google.common.collect.ImmutableSet

object ExpressionMemoryMeter extends PerformanceTest {

  lazy val executor = LocalExecutor(
    new Executor.Warmer.Default,
    Aggregator.min,
    new Executor.Measurer.MemoryFootprint)

  lazy val reporter = new LoggingReporter
  lazy val persistor = Persistor.None

  val sizes = Gen.enumeration("sizes")(6, 7)
  val sops: Set[Operator] = Set(Not, Shr(1), Shr(4), Shr(16), Shl(1), And, Or, Xor, Plus, If0)
  val jops = new ConstraintStore(
    Array("not", "shl1", "shr1", "shr4", "shr16", "xor", "and", "or", "plus", "if0"),
    Array(), Array()) {
    override def isSatisfiable(e: domain.Expression, id: Identifier) = false
  }

  performance of "MemoryFootprint" in {
    performance of "Scala" in {
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
  
  
  private[this] def measureJava(s: Int):java.util.Set[Expression] = {
    val id = new Identifier("x")
    val engine = new SearchEngine(id, GuessVerifier.NONE, SearchEngineMode.PESSIMISTIC)
    engine.getExpressions(s, ImmutableSet.of(id), jops)
  }
}