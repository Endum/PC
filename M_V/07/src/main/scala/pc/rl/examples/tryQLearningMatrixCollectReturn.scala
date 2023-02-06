package pc.rl.examples

import pc.rl.model.QMatrix

object TryQMatrixCollectReturn extends App:

  import pc.rl.model.QMatrix.Action.*
  import pc.rl.model.QMatrix.*
  import scala.collection.mutable.{Set, HashSet}

  val lab = List(
    List("O","O","O","O","#","O"),
    List("O","O","#","O","O","O"),
    List("O","O","O","O","#","O"),
    List("O","#","O","O","O","O"),
    List("O","O","O","O","#","O"),
    List("#","O","#","O","O","O"),
  )

  val collected: Set[(Integer, Integer)] = HashSet()

  val itemsNum = lab.flatten.count(_ == "#")
  var inHand = false
  
  val rl: QMatrix.Facade = Facade(
    width = 6,
    height = 6,
    initial = (0,0),
    terminal = {case _ => false},//{case _ => collected.size == itemsNum},
    reward = { case ((0,0),_) => (collected.clear(), inHand = false, 0)._3;
               case ((a,b),_) if inHand && a <= 1 && b <= 1 && !(a == 1 && b == 1) => (inHand = false, 10)._2;
               case ((a,b),_) if lab(b)(a) == "#" => if (collected.contains((a,b)) || inHand) -10 else (collected.add((a,b)), 10)._2;
               case _ => -1},
    jumps = {case ((a,b),_) if a <= 1 && b <= 1 && !(a == 1 && b == 1) => (1, 1)},
    gamma = 0.9999,
    alpha = 0.5,
    epsilon = 0.3,
    v0 = 1
  )

  val q0 = rl.qFunction
  println(rl.show(q0.vFunction,"%2.1f"))
  val q1 = rl.makeLearningInstance().learn(100_000,300,q0)
  println(rl.show(q1.vFunction,"%2.1f"))
  println(rl.show(s => actionToString(q1.bestPolicy(s)),"%7s"))
  println(rl.show((a,b) => lab(b)(a) == "#", "%2b"))