package icfp

import spire.math.ULong
import org.junit.Test
import org.junit.Assert._

class ULongCmpTest {
    
  @Test
  def testCmp() {
     assertTrue(ulong(1) < ulong(2))
     assertTrue(ulong(1) < "FFFF FFFF FFFF FFFF")
     assertFalse("FFFF FFFF FFFF FFFF" < ulong(1))
     assertTrue("FFFF FFFF FFFF FFFE" < "FFFF FFFF FFFF FFFF")
     assertFalse("FFFF FFFF FFFF FFFF" < "FFFF FFFF FFFF FFFE")
  }
}