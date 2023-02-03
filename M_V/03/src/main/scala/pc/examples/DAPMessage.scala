package pc.examples

import java.util.Random

import pc.modelling.{CTMCSimulation, DAP, DAPGrid}
import pc.utils.{MSet, Grids}
import pc.modelling.CTMCSimulation.*

/*
    The idea is to make a net which propagate the message to every node,
    making them entering another state, waiting for response.
    The receiver is the only one who can start the answere.
    Answer is also propagated to everybody,
    making everybody coming back to their original state.
*/

object DAPMessage:
  enum Place:
    case A,B,C
  type ID = (Int, Int)
  export Place.*
  export pc.modelling.DAP.*
  export pc.modelling.DAPGrid.*
  export pc.modelling.CTMCSimulation.*

  val messageRules = DAP[Place](
    Rule(MSet(A,A), m=>1000,  MSet(A),  MSet()),   // a|a --1000--> a
    Rule(MSet(A),   m=>1,     MSet(A),  MSet(A)),       // a --1--> a|^a
  )
  val messageCTMC = DAP.toCTMC[ID, Place](messageRules)
  val net = Grids.createRectangularGrid(5, 5)
  // an `a` initial on top left
  val state = State[ID,Place](MSet(Token((0, 0), A)), MSet(), net)

@main def mainDAPMessage =
  import DAPMessage.*
  messageCTMC.newSimulationTrace(state,new Random).take(50).toList.foreach(
    step =>
      println(step._1) // print time
      println(DAPGrid.simpleGridStateToString[Place](step._2, A)) // print state, i.e., A's
    )