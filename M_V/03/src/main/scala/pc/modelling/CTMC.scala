package pc.modelling

import java.util.Random

import pc.utils.Time
import pc.modelling.CTMCSimulation.newSimulationTrace

trait CTMC[S]:
  import CTMC.Action
  def transitions(a: S): Set[Action[S]] // rate + state

object CTMC:

  case class Action[S](rate: Double, state: S)
  extension [S](rate: Double)
    def -->(state: S) = Action(rate, state)

  case class Transition[S](state: S, action: Action[S])

  def ofFunction[S](f: PartialFunction[S, Set[Action[S]]]): CTMC[S] =
    s => f.applyOrElse(s, (x: S) => Set[Action[S]]())

  def ofRelation[S](rel: Set[Transition[S]]): CTMC[S] =
    ofFunction(s => rel filter (_.state == s) map (_.action))

  def ofTransitions[S](rel: Transition[S]*): CTMC[S] = ofRelation(rel.toSet)

  def tryTiming[S](ctmc: CTMC[S], s: S, f: S, end: S, n: Int): (Double, Double) = 
    (1 to n).map(
      _ =>
        var tTime: Double = 0
        var fTime: Double = 0
        ctmc.newSimulationTrace(s, new Random)
          .scanLeft(s)((last, x) => (tTime += x.time, if (last == f) fTime += x.time, /*print(" " + x.state),*/ x)._3/*4*/.state)
          .find(_ == end)
        (tTime, fTime)
    ).foldLeft((0, 0):(Double, Double))((a, b) => (a._1 + (b._1 / n), a._2 + (b._2 / n)))