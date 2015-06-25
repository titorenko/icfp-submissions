package icfp

import org.junit.Test
import org.junit.Assert._
import icfp.parser.BVParsers
import spire.math.ULong
import org.junit.Assert
import scala.collection.parallel.ParSet
import spire.math.ULong
import scala.actors.Eval
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.MutableList
import java.util.HashMap
import org.junit.Ignore
import scala.collection.immutable.Set

class SimplificationIntegrationTest {
  val testData: Vector[ULong] = Vector(
      "1", "2", "4", "8", "10", "20", "40", "80", "100", "200", "400", "800", "1000", "2000", "4000",
      "8000", "10000", "20000", "40000", "80000", "100000", "200000", "400000", "800000", "1000000",
      "2000000", "4000000", "8000000", "10000000", "20000000", "40000000", "80000000", "100000000", 
      "200000000", "400000000", "800000000", "1000000000", "2000000000", "4000000000", "8000000000", 
      "10000000000", "20000000000", "40000000000", "80000000000", "100000000000", "200000000000",
      "400000000000", "800000000000", "1000000000000", "2000000000000", "4000000000000", "8000000000000",
      "10000000000000", "20000000000000", "40000000000000", "80000000000000", "100000000000000",
      "200000000000000", "400000000000000", "800000000000000", "1000000000000000", "2000000000000000", 
      "4000000000000000", "8000000000000000", "0", "5", "3", "7", "f", "1f", "3f", "7f", "ff", "1ff", "3ff",
      "7ff", "fff", "1fff", "3fff", "7fff", "ffff", "1ffff", "3ffff", "7ffff", "fffff", "1fffff", "3fffff",
      "7fffff", "ffffff", "1ffffff", "3ffffff", "7ffffff", "fffffff", "1fffffff", "3fffffff", "7fffffff",
      "ffffffff", "1ffffffff", "3ffffffff", "7ffffffff", "fffffffff", "1fffffffff", "3fffffffff",
      "7fffffffff", "ffffffffff", "1ffffffffff", "3ffffffffff", "7ffffffffff", "fffffffffff", "1fffffffffff",
      "3fffffffffff", "7fffffffffff", "ffffffffffff", "1ffffffffffff", "3ffffffffffff", "7ffffffffffff",
      "fffffffffffff", "1fffffffffffff", "3fffffffffffff", "7fffffffffffff", "ffffffffffffff",
      "1ffffffffffffff", "3ffffffffffffff", "7ffffffffffffff", "fffffffffffffff", "1fffffffffffffff", 
      "3fffffffffffffff", "7fffffffffffffff", "ffffffffffffffff", "fffffffffffffffe", "fffffffffffffffc",
      "fffffffffffffff8", "fffffffffffffff0", "ffffffffffffffe0", "ffffffffffffffc0", "ffffffffffffff80",
      "ffffffffffffff00", "fffffffffffffe00", "fffffffffffffc00", "fffffffffffff800", "fffffffffffff000",
      "ffffffffffffe000", "ffffffffffffc000", "ffffffffffff8000", "ffffffffffff0000", "fffffffffffe0000",
      "fffffffffffc0000", "fffffffffff80000", "fffffffffff00000", "ffffffffffe00000", "ffffffffffc00000",
      "ffffffffff800000", "ffffffffff000000", "fffffffffe000000", "fffffffffc000000", "fffffffff8000000",
      "fffffffff0000000", "ffffffffe0000000", "ffffffffc0000000", "ffffffff80000000", "ffffffff00000000",
      "fffffffe00000000", "fffffffc00000000", "fffffff800000000", "fffffff000000000", "ffffffe000000000",
      "ffffffc000000000", "ffffff8000000000", "ffffff0000000000", "fffffe0000000000", "fffffc0000000000",
      "fffff80000000000", "fffff00000000000", "ffffe00000000000", "ffffc00000000000", "ffff800000000000",
      "ffff000000000000", "fffe000000000000", "fffc000000000000", "fff8000000000000", "fff0000000000000",
      "ffe0000000000000", "ffc0000000000000", "ff80000000000000", "ff00000000000000", "fe00000000000000",
      "fc00000000000000", "f800000000000000", "f000000000000000", "e000000000000000", "c000000000000000",
      "ce95a65102370d57", "46e3939c13b15e3e", "a0e012fc0531b6d4", "4ab43b1bf2c26abd", "6af62cd32ea6a286",
      "279229b83f0aa6b0", "ffa5a16414681a64", "d80212352ab3c411", "c05a0ecfd5bffb44", "f55a3e102f460191",
      "ca7a305e2d356717", "d41e2282eec1d38d", "12dd2910945743ed", "05df9bb46f3b58e8", "fbe33f9be1cab3c3",
      "4c104c0aa009b5e0", "0ea5e63634c3431d", "08f57a7d7428f6b8", "a1658cea36c90191", "626782fe0b074be5",
      "4bb489afa4fb64fb", "bbbade921385c315", "551c1e6138f0a078", "979e47a31eae45dc", "df12523c24107b7b",
      "3e19328478e49df8", "9347809b48e5b4a4", "a144719cf5fb20fd", "29fb81a7471b4d46", "f600fef0e1b1a50b",
      "645d73a1011707b4", "8328920723898019", "a3071599c04df0a5", "7e2ee28ad4d26e22", "e20ed29f6fd6be08",
      "85ad8e2d42d6889e", "234b7e67a9af0bd2", "9603e2d037adc7d4", "8581ded37abf7373", "fc33b72a548f505c",
      "aaadcc3929c003fd", "72a4ab1717a92691", "e5b4a9b1ab016070", "b4dec7b176a3b1aa", "84c62b41ccf9b696",
      "7e9a630dc2459454", "f832066c8645ebac", "b43f01472af0988d", "3a56487f401e2464", "180e7009d0ab5829",
      "e6a824ae01f020fe", "2a62d6ac2d3d51d7", "9c14007db5c04571", "564efe49086b3eb9", "13bdb0304baa80c8",
      "8f21d1964171cefe", "9b4d235f4d571ed9", "210b5376878ebe4a", "245edb0db73ccffc", "945f473ed426b672",
      "ae5f6e4d8d12f1aa", "0a032b469ef2ca8f", "2ceb0376d28cab46", "4621fe1a3ab0d951", "3cafe41e3bdea60b")
      
//  val ops: Set[Operator] = Set(Not, Shr(1), Shr(4), Shr(16), Shl(1), Xor, And, Or, Plus, If0)
   val ops: Set[Operator] = Set(Shr(4), Shr(1), Shl(1), And, Xor, If0, Shr(16), Or, Plus)
   val size = 7
      
  @Test
  def testSimplify() {
    val simplified = ExpressionGenerator(ops).generate(size)
    val simplifiedMap = new ConcurrentHashMap[Vector[ULong], Expression]()
    simplified.par.foreach(e => simplifiedMap.put(e.eval(testData), e))

    val all = ExpressionGenerator(ops, false).generate(size)
    
    println("Compression %d -> %d".format(all.size, simplified.size));
    all.par.foreach{e =>
      assertIsReducibleTo(e, simplified, simplifiedMap); 
    }
  }
  
  private[this] def assertIsReducibleTo(e: Expression, pool: Set[Expression],
      valueDictionary: java.util.Map[Vector[ULong], Expression]): Unit = {
    if (!pool.contains(e)) {
      val eVals = e.eval(testData)
      Assert.assertTrue("Could not find equivalent for "+e, valueDictionary.get(eVals) != null)
    }
  } 
  
  
  @Test 
  def findEquivalent() {
    val simplified = ExpressionGenerator(ops).generate(size)
    val simplifiedMap = scala.collection.mutable.Map[Vector[ULong], List[Expression]]()
    simplified.foreach(e => {
      val key = e.eval(testData)
      simplifiedMap.put(key, e :: simplifiedMap.getOrElse(key, List()))
    })
    println("Populated "+simplifiedMap.size+" elements from "+simplified.size);
    
    val nonTrivialEq = simplifiedMap.values.filter(_.size > 1).toList
    val nonTrivialEqSorted = nonTrivialEq.sortBy(-_.size)
    nonTrivialEqSorted.take(10).foreach(l => println(l.size+" "+l))
    println(nonTrivialEq.size)
  }
}