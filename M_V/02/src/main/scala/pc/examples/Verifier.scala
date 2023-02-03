package pc.examples

export pc.modelling.PetriNet
import pc.utils.MSet

object Verifier:
  
  enum Place:
    case P1, P2, P3, P4, P5, P6, P7
    
  export Place.*
  export pc.modelling.PetriNet.*
  export pc.modelling.SystemAnalysis.*
  export pc.utils.MSet

  def pnRW = PetriNet[Place](
    /* T1 */ MSet(P1) ~~> MSet(P2),
    /*T2-3*/ MSet(P2) ~~> MSet(P3, P4),
    /* T4 */ MSet(P3, P5) ~~> MSet(P6, P5),
    /* T6 */ MSet(P6) ~~> MSet(P1),
    /* T5 */ MSet(P4, P5) ~~> MSet(P7) ^^^ MSet(P6),
    /* T7 */ MSet(P7) ~~> MSet(P5, P1),
  ).toSystem

@main def mainVerifier =
  import Verifier.*
  // Safe properties check.
  (
    pnRW.checkSafetyProperty(MSet(P1,P1,P5), 15, (l: MSet[Place]) => 
      l.asList.count(_ equals P7).compare(2).equals(-1) &&
      (l.asList.count(_ equals P7).compare(0).equals(0) || l.asList.count(_ equals P6).compare(0).equals(0))
    )
  ) match
      case true => println("Safety property checked.")
      case false => println("Safety property not respected.")
        
            

