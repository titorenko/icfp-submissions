package icfp

import scala.collection.mutable.{Set => MSet}
import scala.collection.mutable.HashSet
import scala.collection.immutable.Set
import scala.collection.mutable.LinkedHashSet

object ExpressionGenerator {
  def apply(operators: Set[Operator], simplify: Boolean = true): ExpressionGenerator = {
    
    val simplifier: Expression => Option[Expression] = if (simplify) e => e.simplify else e => Some(e) 

    val unary: Set[Generator] = operators.flatMap(_ match {
      case Not => Some((e: Expression) => simplifier(NotExpr(e)))
      case Shl(s) => Some((e: Expression) => simplifier(ShlExpr(s, e)))
      case Shr(s) => Some((e: Expression) => simplifier(ShrExpr(s, e)))
      case _ => None
    })
    
    val binary: Set[Generator2] =  operators.flatMap(_ match {
      case And  => Some((e1: Expression, e2: Expression) => simplifier(AndExpr(e1, e2)))
      case Xor  => Some((e1: Expression, e2: Expression) => simplifier(XorExpr(e1, e2)))
      case Or   => Some((e1: Expression, e2: Expression) => simplifier(OrExpr(e1, e2)))
      case Plus => Some((e1: Expression, e2: Expression) => simplifier(PlusExpr(e1, e2)))
      case _ => None
    })
    
    val tfold: Option[Generator2] = 
      if (operators.contains(TFold)) {
        Some((e1: Expression, e2: Expression) => simplifier(FoldExpr(e1, Number(0), e2)))
      }  else {
        None
      }
    
    val ternary: Set[Generator3] = operators.flatMap(_ match {
      case If0 => 
        Some((e1: Expression, e2: Expression, e3: Expression) => simplifier(If0Expr(e1, e2, e3)))
      case Fold => 
        Some((e1: Expression, e2: Expression, e3: Expression) => simplifier(FoldExpr(e1, e2, e3)))
      case _ => None
    })

    new ExpressionGenerator(unary.toVector, binary.toVector, tfold, ternary.toVector)
  }
}

class ExpressionGenerator(unary: Vector[Generator], binary: Vector[Generator2], tfold: Option[Generator2], ternaryGens: Vector[Generator3]) {
  
  val cache = collection.mutable.Map[(Int, Set[Expression]), MSet[Expression]]()

  def generate(size: Int, isTopSizeOnly: Boolean = false): Set[Expression] = {
    val topSizeExprs = generateCached(size, xvars)
    if (isTopSizeOnly) topSizeExprs.toSet else cache.values.reduce(_ ++ _).toSet
  }
  
  def generateCached(size: Int, vars: Set[Expression]): MSet[Expression] = {
    cache.getOrElseUpdate((size, vars), {
      generateInternal(size, vars)
    })
  }

  def generateInternal(size: Int, vars: Set[Expression]): MSet[Expression] = {
    if (size <= 0) MSet()
    else if (size == 1) MSet(Number(0), Number(1)) ++ vars
    else {
      val inner = generateCached(size - 1, vars)
      val map = LinkedHashSet[Expression]()
      getUnary(inner, map)
      val lhsSize = (1 to (size-1) /2)
      lhsSize.foreach(s => getBinary( generateCached(s, vars), generateCached(size-1-s, vars), map))
      val ternary = if (ternaryGens.nonEmpty && size > 3) {
        (1 to (size-3)).foreach(conditionSize => (1 to (size-2-conditionSize)).foreach(ifSize => { //think about /2
        	val thenSize = size-conditionSize-ifSize-1
        	getTernary(generateCached(conditionSize, vars), generateCached(ifSize, vars), generateCached(thenSize, vars), map)
        }))
      } else MSet()
      map
    }
  }
  
  private val xvars: Set[Expression] =  Set(VarX)

  private[this] def getUnary(expressions: Iterable[Expression], result: MSet[Expression]): Unit = {
    expressions.foreach(e => unary.foreach(g => {
      val ue = g(e)
      if (ue.isDefined) result += ue.get
    }))
  }
  
  private[this] def getBinary(lhs: Iterable[Expression], rhs: Iterable[Expression], result: MSet[Expression]): Unit = {
    lhs.foreach(l => (rhs.foreach(r => ( binary.foreach(g => {
      val be = g(l,r)
      if (be.isDefined) result += be.get
    })))))
  }
  
  private[this] def getTernary(e0: Iterable[Expression], e1: Iterable[Expression], e2: Iterable[Expression], result: MSet[Expression]): Unit = {
    e0.foreach(v0 => (e1.foreach(v1 => e2.foreach( v2 => (ternaryGens.foreach(g => {
      val te = g(v0, v1, v2)
      if (te.isDefined) result += te.get
    }))))))
  }
}
