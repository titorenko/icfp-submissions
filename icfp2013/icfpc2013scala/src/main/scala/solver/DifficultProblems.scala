package solver

import problems.TrainProblemDatabase
import icfp.ExpressionGenerator

object DifficultProblems extends App {
  val id = "2QPBAAKt3qZG5vLaErqTdCgq"
  val p = TrainProblemDatabase.readProblem(id)
  println("Problem p "+p)
  println("With solution "+p.solution+" of size "+p.solution.get.size)
  val generator = ExpressionGenerator(p.ops, true)
  val expressions = generator.generate(12)
  println("Generated "+expressions.size);
  val result = new NaiveMemorySearcher(expressions, p).find
  println(result);
	
}
/*Solving 825 problems
Solving Problem(01kqAQ87VkNLGfMULiEJhdoe) of size 25 with Set(Shr(4), Shl(1), Xor, If0, Plus, Or)
Finished solving in 1499 millis
Solution: None
size 11 Some((plus 1 (or 1 (shr4 (plus (shr4 x) (xor 1 x))))))

Solving Problem(2QPBAAKt3qZG5vLaErqTdCgq) of size 18 with Set(Shr(4), Shr(1), Shl(1), And, Xor, If0, Shr(16), Or)
Finished solving in 2637 millis
Solution: None

Size 11 - Generated 16709214
None


Solving Problem(2aVSXHjxOmdibDRq4NWs0cMX) of size 30 with Set(Shr(4), Shr(1), Shl(1), And, If0, Plus, Not, Shr(16), Or)
Finished solving in 15678 millis
Solution: None

Solving Problem(0Bp5BK7dAibHAJyv1TGfnMlR) of size 28 with Set(Shr(4), Shr(1), Shl(1), And, Xor, If0, Plus, Not, Shr(16), Or)
Finished solving in 22667 millis
Solution: None*/