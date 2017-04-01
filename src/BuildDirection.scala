
object BuildDirection extends Enumeration {
  type BuildDirection = Value
  // back and front are relative to the choke point
  // this assumes of course that your main only has one chokepoint
  val MAIN_BACK,
  MAIN_MIDDLE,
  MAIN_FRONT,
  NATURAL_CHOKE,
  ENEMY_MAIN_CHOKE_IN,
  ENEMY_NATURAL_MIDDLE,
  ENEMY_MAIN_CHOKE_OUT = Value
}