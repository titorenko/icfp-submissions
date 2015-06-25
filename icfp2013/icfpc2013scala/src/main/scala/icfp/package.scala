import spire.math.ULong
import java.lang.Long
import scala.math.BigInt
import scala.language.implicitConversions

package object icfp {
  type Generator = Expression => Option[Expression]
  type Generator2 = (Expression, Expression) => Option[Expression]
  type Generator3 = (Expression, Expression, Expression) => Option[Expression]
  
  implicit def ulong(str: String): ULong = {
    ULong(BigInt(str.replaceAll("\\s+|x", ""), 16).toLong)
  }

  @inline implicit def ulong(i: Int): ULong = {
    ULong(i)
  }
  
  def min(u1: ULong, u2: ULong): ULong = {
    if (u1 < u2) u1 else u2
  }

  def max(u1: ULong, u2: ULong): ULong = {
    if (u1 > u2) u1 else u2
  }
}