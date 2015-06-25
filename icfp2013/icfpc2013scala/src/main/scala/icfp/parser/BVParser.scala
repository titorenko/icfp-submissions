package icfp.parser

import scala.util.parsing.combinator.RegexParsers

import icfp.AndExpr
import icfp.Expression
import icfp.FoldExpr
import icfp.If0Expr
import icfp.NotExpr
import icfp.Number
import icfp.OrExpr
import icfp.PlusExpr
import icfp.ShlExpr
import icfp.ShrExpr
import icfp.VarA
import icfp.VarX
import icfp.VarY
import icfp.XorExpr
import spire.math.ULong

class BVParsers(val xstr: String = "x", val ystr: String = "y", val astr: String = "a") extends RegexParsers {
  
  def parseProgram(input: String) = parse(program, input)
  def parseExpression(input: String) = parse(expr, input)
  
  private[this] def parse(parser: Parser[Expression], input: String): Expression = parseAll(parser, input) match {
    case Success(e, in) => e
    case Failure(msg, in) => fail(input, msg)
    case Error(msg, in) => fail(input, msg) 
  }  
  
  private[this] def fail(input: String, msg: String) = throw new RuntimeException("Failed to parse BV expression "+input+": "+msg)
  
  private[this] def program: Parser[Expression] = "("~"lambda"~"("~xvar~")"~>expr<~")" 
  
  private[this] def expr: Parser[Expression] = number | id | unary | binary | if0 | fold
  private[this] def xexpr: Parser[Expression] = number | xvar | unary | binary | if0 | fold
  
  private[this] def id = yvar | avar | xvar
  private[this] def number: Parser[Number] = ("0" | "1") ^^ (value => Number(ULong(Integer.parseInt(value))))
  private[this] def unary: Parser[Expression] = "("~> (not | shr(1) | shr(4) | shr(16) | shl1) <~ ")"  
  private[this] def binary: Parser[Expression] = "("~> (and | or | xor | plus) <~ ")"
  
  private[this] def if0: Parser[Expression] = "("~"if0"~>expr~expr~expr<~")" ^^ {case e0~e1~e2 => If0Expr(e0, e1, e2)}
  private[this] def fold: Parser[Expression] = "("~"fold"~>xexpr~xexpr~"("~"lambda"~"("~yvar~avar~")"~expr<~")"~")" ^^ 
		  {case e0~e1~"("~"lambda"~"("~y~a~")"~e2 => FoldExpr(e0, e1, e2, foldVars) }
  
  lazy val foldVars: Set[Expression] = if (xstr == ystr || xstr == astr) Set(VarY, VarA) else Set(VarX, VarY, VarA)  
  
  private[this] def and: Parser[Expression] = "and"~>expr~expr ^^ { case left~right => AndExpr(left, right) }
  private[this] def or: Parser[Expression] = "or"~>expr~expr ^^ { case left~right => OrExpr(left, right) }
  private[this] def xor: Parser[Expression] = "xor"~>expr~expr ^^ { case left~right => XorExpr(left, right) }
  private[this] def plus: Parser[Expression] = "plus"~>expr~expr ^^ { case left~right => PlusExpr(left, right) }
  
  private[this] def not: Parser[Expression] = 
    "not"~>expr ^^ (e => NotExpr(e))
  private[this] def shl1: Parser[Expression] =
    "shl1"~>expr ^^ (e => ShlExpr(1, e))
  private[this] def shr(shift: Byte): Parser[Expression] = 
    ("shr"+shift)~>expr ^^ (e => ShrExpr(shift, e))
  
  private[this] def xvar: Parser[Expression] =  xstr ^^ (_ => VarX) 
  private[this] def yvar: Parser[Expression] =  ystr ^^ (_ => VarY) 
  private[this] def avar: Parser[Expression] =  astr ^^ (_ => VarA) 
}