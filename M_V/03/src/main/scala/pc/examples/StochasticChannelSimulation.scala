package pc.examples

import pc.utils.Time
import java.util.Random
import pc.examples.StochasticChannel.*
import pc.modelling.CTMC

@main def mainStochasticChannelSimulation =
  /*Time.timed(
    println(stocChannel.newSimulationTrace(IDLE, new Random)
                           .take(10)
                           .toList
                           .mkString("\n")))*/

  val times = CTMC.tryTiming(stocChannel, IDLE, FAIL, DONE, 1_000_000)
  println("Approximated time elapsed: " + times._1 + "\nApproximated time in fail: " + times._2)
