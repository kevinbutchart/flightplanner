package utils

/**
 * Created by kevin on 25/03/15.
 */
object Bearing {
  def normalise(angle : Double) = math.round((angle + 360)%360).toDouble

}
