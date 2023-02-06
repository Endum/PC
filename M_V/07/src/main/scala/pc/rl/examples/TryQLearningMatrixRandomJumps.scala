

import pc.rl.model.QMatrix

object TryQMatrixRandomJumps extends App:

  import pc.rl.model.QMatrix.Action.*
  import pc.rl.model.QMatrix.*

  val rand = new scala.util.Random

  val rl: QMatrix.Facade = Facade(
    width = 9,
    height = 9,
    initial = (0,0),
    terminal = {case (8,8)=>true; case _=>false},
    reward = { case ((b,a),_) if b%2==1&&((b%4==1&&a%2==0)||(b%4==3&&a%2==1)) => -1; case((8,8),_) => 10; case _ => -1},
    jumps = { case ((b,a),_) if b%2==1&&((b%4==1&&a%2==0)||(b%4==3&&a%2==1)) => (rand.nextInt(9),rand.nextInt(9)) },
    gamma = 0.9,
    alpha = 0.5,
    epsilon = 0.3,
    v0 = 1
  )

  val q0 = rl.qFunction
  println(rl.show(q0.vFunction,"%2.1f"))
  val q1 = rl.makeLearningInstance().learn(80000,100,q0)
  println(rl.show(q1.vFunction,"%2.1f"))
  println(rl.show(s => actionToString(q1.bestPolicy(s)),"%7s"))
  println(rl.show((b,a) => b%2==1&&((b%4==1&&a%2==0)||(b%4==3&&a%2==1)), "%2b"))
