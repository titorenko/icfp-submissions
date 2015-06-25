package icfp

object Operator {
  def fromString(op: String): Operator = op.trim() match {
    case "shr1" => Shr(1)
    case "shr4" => Shr(4)
    case "shr16"=> Shr(16)
    case "shl1" => Shl(1)
    case "not"  => Not
    case "xor"  => Xor
    case "and"  => And
    case "or"   => Or
    case "plus" => Plus
    case "if0"  => If0
    case "tfold"=> TFold
    case "fold" => Fold
    case "bonus"=> Bonus
    case _ => throw new NotImplementedError(op)
  }
}

sealed abstract class Operator 

case class Shr(shift: Int) extends Operator

case class Shl(shift: Int) extends Operator

case object Not extends Operator

case object Xor extends Operator

case object And extends Operator

case object Or extends Operator

case object Plus extends Operator

case object If0 extends Operator

case object Fold extends Operator

case object TFold extends Operator

case object Bonus extends Operator