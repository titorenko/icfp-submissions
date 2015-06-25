package icfp

import spire.math.ULong
import java.lang.Long

object Context {
  def apply(x: ULong): Context = {
    new Context(x, None, None)
  }
}

class Context(val x: ULong, val y: Option[ULong], val a: Option[ULong]) {
  def getValue(v: Variable): ULong = {
    v match {
    	case VarX => x
    	case VarY => y.get
    	case VarA => a.get
    }
  }
  
  def bindFold(y: ULong, a: ULong): Context = {
    new Context(x, Some(y), Some(a))
  }
}