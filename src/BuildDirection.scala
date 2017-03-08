
object BuildDirection extends Enumeration {
  type BuildDirection = Value
  // back and front are relative to the choke point
  // this assumes of course that your main only has one chokepoint
  val MAIN_BACK, MAIN_MIDDLE, MAIN_FRONT, NATURAL_CHOKE = Value
}