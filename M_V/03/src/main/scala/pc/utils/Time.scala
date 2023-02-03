package pc.utils

object Time {

  // facility to track time, just embed the computation in the input
  def timed[A](v: =>A):(A, Double) = {
    val t0 = java.lang.System.nanoTime
    try{ (v, (java.lang.System.nanoTime-t0)/1000000) } finally println("Timed op (msec): "+(java.lang.System.nanoTime-t0)/1000000)
  }
}
