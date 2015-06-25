package icfp

import spire.math.UInt
import spire.math.ULong
import scala.runtime.ScalaRunTime
import ULongConstants._
import scala.collection.parallel.immutable.ParVector

sealed abstract class Expression {
  def eval(ctx: Context): ULong
  def eval(args: Vector[ULong]): Vector[ULong] = {
    args.map(x => eval(Context(x)))
  }
  def size: Int
  def getMaxValue(): ULong = MAX
  def simplify(): Option[Expression] = Some(this)
  
  override lazy val hashCode: Int = { ScalaRunTime._hashCode(this.asInstanceOf[Product]) }
}

trait Variable {
  def size = 1
  @inline def eval(ctx: Context): ULong = ctx.getValue(this)
}

case object VarX extends Expression with Variable {
  override def toString() = "x"
}
case object VarY extends Expression with Variable {
  override def toString() = "y"
}
case object VarA extends Expression with Variable {
  override def toString() = "a"
}

case class Number(value: ULong) extends Expression {
  def getArity(): Int = 0
  def size = 1
  override def getMaxValue(): ULong = value
  @inline def eval(ctx: Context): ULong = value
  override def toString() = BigInt(value.toString()).toString(16)
}


case class NotExpr(val arg: Expression) extends Expression {
  def eval(ctx: Context): ULong = ~arg.eval(ctx)
  override def simplify(): Option[Expression] = arg match {
    case NotExpr(_) => None
    case XorExpr(Number(n), e) => Some(XorExpr(Number(~n), e))
    case XorExpr(e, Number(n)) => Some(XorExpr(Number(~n), e))
    case Number(n) => Some(Number(~n))
    case _ => Some(this)
  }
  def size = 1 + arg.size
  override def toString() = s"(not $arg)"
}

case class ShlExpr(val shift: Int, val arg: Expression) extends Expression {
  def eval(ctx: Context): ULong = arg.eval(ctx) << shift
  
  override def simplify(): Option[Expression] = arg match {
    case ShlExpr(shift2, inner) => Some(ShlExpr(shift + shift2, inner))
    case ShrExpr(1, ShlExpr(1, e)) =>  if (shift == 1) None else Some(this) //(shl1 (shr1 (shl1 x))) = (shl1 x))
    case Number(n) => Some(Number(n << shift))
    case _ => Some(this)
  }
  
  override def getMaxValue(): ULong = max(arg.getMaxValue, arg.getMaxValue << shift)
  def size = 1 + arg.size
  override def toString() = s"(shl$shift $arg)"
}

case class ShrExpr(val shift: Int, val arg: Expression) extends Expression{
  def maxCutoff: ULong = (ulong(1) << shift)
  
  def eval(ctx: Context): ULong = arg.eval(ctx) >> shift
  
  override def simplify(): Option[Expression] = arg match {
    case ShrExpr(shift2, inner) => if (shift + shift2 >= 63) None else Some(ShrExpr(shift + shift2, inner))
    case Number(n) => Some(Number(n >> shift))
    case ShlExpr(sl, ShrExpr(sr, e)) =>  if (sl<=shift && sl <= sr) Some(ShrExpr(shift+sr-sl, e)) else Some(this)
    case XorExpr(e, Number(n)) => simplifySmallNumbers(n)
    case XorExpr(Number(n), e) => simplifySmallNumbers(n)
    case OrExpr(e, Number(n)) => simplifySmallNumbers(n)
    case OrExpr(Number(n), e) => simplifySmallNumbers(n)
    
    case _ => if (arg.getMaxValue < maxCutoff) None else Some(this)
  }
  
  override def getMaxValue(): ULong = arg.getMaxValue >> shift
  def size = 1 + arg.size
  override def toString() = s"(shr$shift $arg)"
  
  private[this] def simplifySmallNumbers(n: ULong): Option[icfp.Expression] = {
    if ((n >> shift) == ZERO) None else Some(this)
  }
}


final case class XorExpr(val left: Expression, val right: Expression) extends Expression {
  def eval(ctx: Context): ULong = left.eval(ctx) ^ right.eval(ctx)
  
  override def equals(other: Any) = {
    other match {
      case that: icfp.XorExpr =>  
        (left == that.left && right == that.right) || (left == that.right && right == that.left)  
      case _ => false
    }
  }
  
  override def simplify(): Option[Expression] = (left, right) match {
    case (Number(n1), Number(n2)) => Some(Number(n1 ^ n2))
    case (Number(ZERO), _) | (_, Number(ZERO)) => None
    case (_, Number(MAX)) | (Number(MAX), _)=> None
    case (NotExpr(_), NotExpr(_)) => None  // xor(not(a), not(b)) = xor(a,b)
    case (NotExpr(e1), e2) => moveNotOut(e1, e2) // xor(not(a), b) = not(xor(a,b))   
    case (e1, NotExpr(e2)) => moveNotOut(e1, e2)
    case (e1, XorExpr(e2, e3)) => if (e1 == e2 || e1 == e3) None else Some(this)
    case (l, r) => if (l == r || r == NotExpr(l)) None else Some(this)
  }
  
  private[this] def moveNotOut(e1: Expression, e2: Expression): Option[Expression] = {
    val simplified = XorExpr(e1, e2).simplify 
    if (simplified.isEmpty) None else NotExpr(simplified.get).simplify 
  }
  
  def size = 1 + left.size + right.size
  
  override lazy val hashCode: Int = Xor.hashCode() + 41*(left.hashCode + right.hashCode)
  
  override def toString() = s"(xor $left $right)"
}

case class OrExpr(val left: Expression, val right: Expression) extends Expression {
  def eval(ctx: Context): ULong = left.eval(ctx) | right.eval(ctx)
  
  override def simplify(): Option[Expression] = (left, right) match {
    case (Number(n1), Number(n2)) => Some(Number(n1 | n2))
    case (Number(ZERO), _) | (_, Number(ZERO)) => None 
    case (_, Number(MAX)) | (Number(MAX), _)=> None 
    case (NotExpr(e1), NotExpr(e2)) => {
      val sand = AndExpr(e1, e2).simplify
      if (sand.isDefined) Some(NotExpr(sand.get)) else None 
    }
    case (e1, OrExpr(e2, e3)) => if (e1 == e2 || e1 == e3) None else Some(this)
    case (l, r) => if (l == r || r == NotExpr(l)) None else Some(this)
  }
  
  override def getMaxValue(): ULong = left.getMaxValue | right.getMaxValue
  def size = 1 + left.size + right.size
  override def toString() = s"(or $left $right)"
  
  override lazy val hashCode: Int = Or.hashCode() + 67*(left.hashCode + right.hashCode)
  
  override def equals(other: Any) = {
    other match {
      case that: icfp.OrExpr =>  
        (left == that.left && right == that.right) || (left == that.right && right == that.left)  
      case _ => false
    }
  }
}

case class AndExpr(val left: Expression, val right: Expression) extends Expression {
  def eval(ctx: Context): ULong = left.eval(ctx) & right.eval(ctx)
  
  override def simplify(): Option[Expression] = (left, right) match {
    case (Number(n1), OrExpr(Number(n2), e)) => {
      val sand = AndExpr(Number(n1), e).simplify
      if (sand.isDefined) Some(OrExpr(Number(n1 & n2), sand.get)) else Some(Number(n1 & n2))
    }
    
    case (Number(n1), Number(n2)) => Some(Number(n1 & n2))
    case (Number(ZERO), _) | (_, Number(ZERO)) => None 
    case (_, Number(MAX)) | (Number(MAX), _)=> None 
    case (Number(n), ShlExpr(s, e)) => if (n >> s == ZERO) None else Some(this)
    case (Number(n), NotExpr(ShlExpr(s, e))) => if (n >> s == ZERO) None else Some(this)
    case (NotExpr(e1), NotExpr(e2)) => {
      val sor = OrExpr(e1, e2).simplify
      if (sor.isDefined) Some(NotExpr(sor.get)) else None 
    }
    case (e1, AndExpr(e2, e3)) => if (e1 == e2 || e1 == e3) None else Some(this)
    case (l, r) => if (l == r || r == NotExpr(l)) None else Some(this)
  }
  
  override def getMaxValue(): ULong = min(left.getMaxValue, right.getMaxValue) 
  def size = 1 + left.size + right.size
  override lazy val hashCode: Int = And.hashCode() + 59*(left.hashCode + right.hashCode)
  
  override def equals(other: Any) = {
    other match {
      case that: icfp.AndExpr =>  
        (left == that.left && right == that.right) || (left == that.right && right == that.left)  
      case _ => false
    }
  }
  
  override def toString() = s"(and $left $right)"
}

case class PlusExpr(val left: Expression, val right: Expression) extends Expression {
  def eval(ctx: Context): ULong = left.eval(ctx) + right.eval(ctx)
  
  override def simplify(): Option[Expression] = (left, right) match {
    case (Number(n1), Number(n2)) => Some(Number(n1 + n2))
    case (Number(ZERO), _) | (_, Number(ZERO)) => None
    case (e1, e2) => if (e1 == e2) ShlExpr(1, e1).simplify else if (e2 == NotExpr(e1)) None else Some(this)
  }
  
  override def getMaxValue(): ULong = {
    val m1 = left.getMaxValue
    val m2 = right.getMaxValue
    val sum = m1 + m2
    if (sum < m1 | sum < m2) MAX else sum
  } 
  
  def size = 1 + left.size + right.size
  override lazy val hashCode: Int = Plus.hashCode() + 73*(left.hashCode + right.hashCode)
  override def equals(other: Any) = {
    other match {
      case that: icfp.PlusExpr =>  
        (left == that.left && right == that.right) || (left == that.right && right == that.left)  
      case _ => false
    }
  }
  override def toString() = s"(plus $left $right)"
}

case class If0Expr(val cond: Expression, val e1: Expression, val e2: Expression) extends Expression {
  def eval(ctx: Context): ULong = if (cond.eval(ctx) == ZERO) e1.eval(ctx) else e2.eval(ctx)
  override def toString() = "(if0 " + cond + " " + e1 + " " + e2 + ")"
  
  override def simplify(): Option[Expression] = (cond, e1, e2) match {
    case (Number(_), _, _) => None
    case (OrExpr(Number(_), _), _, _) | (OrExpr(_, Number(_)), _, _) => None
   // case (PlusExpr(Number(_), _), _, _) | (PlusExpr(_, Number(_)), _, _) => None
    case (VarX, Number(ZERO), VarX) => None
    case (VarX, Number(ZERO), ShrExpr(_, VarX)) => None
    case (VarX, Number(ZERO), ShlExpr(_, VarX)) => None
    case (VarX, Number(ZERO), AndExpr(_, VarX)) => None
    case (VarX, Number(ZERO), AndExpr(VarX, _)) => None
    case (VarX, e1, e2) => if (containsx(e1) || e1 == e2) None else Some(this)
    case (_, e1, e2) => if (e1 == e2) None else Some(this)  
  }
  
  private[this] def containsx(e: Expression): Boolean = e match {
    case VarA | VarY | Number(_) => false
    case VarX => true
    case NotExpr(e) => containsx(e)
    case ShlExpr(s, e) => containsx(e)
    case ShrExpr(s, e) => containsx(e)
    case AndExpr(e1, e2) => containsx(e1) | containsx(e2)
    case OrExpr(e1, e2) => containsx(e1) | containsx(e2)
    case XorExpr(e1, e2) => containsx(e1) | containsx(e2)
    case PlusExpr(e1, e2) => containsx(e1) | containsx(e2)
    case If0Expr(e0, e1, e2) => containsx(e0) | containsx(e1) | containsx(e2)
    case FoldExpr(e0, e1, e2, _) => containsx(e0) | containsx(e1) | containsx(e2)
    case _ => ???
  }
  def size = 1 + cond.size + e1.size  + e2.size
  override def getMaxValue(): ULong = max(e1.getMaxValue, e2.getMaxValue)
}

case class FoldExpr(
    val y: Expression, val a: Expression, val e: Expression, 
    val foldVars: Set[Expression] = Set(VarX, VarY, VarA)) extends Expression {
  def eval(ctx: Context): ULong = evalFold(ctx, y.eval(ctx), a.eval(ctx), e)

  private[this] def evalFold(ctx: Context, toFold: ULong, acc: ULong, e: Expression): ULong = {
    var y = toFold
    var a = acc
    (1 to 8) foreach { _ =>
      a = e.eval(ctx.bindFold(y & BYTE_MASK, a))
      y = y >> 8
    }
    a
  }
  def size = 2 + y.size + a.size + e.size
  override def toString() = "(fold " + y + " " + a + " (lambda (y a) " + e + "))"
}