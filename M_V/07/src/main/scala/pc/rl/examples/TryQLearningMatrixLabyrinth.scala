package pc.rl.examples

import pc.rl.model.QMatrix

object TryQMatrixLabyrinth extends App:

  import pc.rl.model.QMatrix.Action.*
  import pc.rl.model.QMatrix.*

  val lab = List(
    List("O","O","O","#","#","O"),
    List("#","O","#","O","O","O"),
    List("O","O","#","O","#","O"),
    List("O","#","#","O","O","O"),
    List("O","O","O","O","#","O"),
    List("#","O","#","O","#","O"),
  )

  val rl: QMatrix.Facade = Facade(
    width = 6,
    height = 6,
    initial = (0,0),
    terminal = {case _=>false},
    reward = { case ((a,b),_) if lab(b)(a) == "#" => -10; case ((5,5),_) => 10; case _ => -1},
    jumps = { case ((a,b),_) if lab(b)(a) == "#" => (0,0) },
    gamma = 0.9,
    alpha = 0.5,
    epsilon = 0.3,
    v0 = 1
  )

  val q0 = rl.qFunction
  println(rl.show(q0.vFunction,"%2.1f"))
  val q1 = rl.makeLearningInstance().learn(20000,100,q0)
  println(rl.show(q1.vFunction,"%2.1f"))
  println(rl.show(s => actionToString(q1.bestPolicy(s)),"%7s"))
  println(rl.show((a,b) => lab(b)(a) == "#", "%2b"))