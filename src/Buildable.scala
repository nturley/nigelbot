class Buildable (val unit:bwapi.UnitType, val tech:bwapi.TechType, val isUnit:Boolean){
  val isTech = !isUnit
  def this(unit:bwapi.UnitType) = this(unit, null, true)
  def this(tech:bwapi.TechType) = this(null, tech, false)

  def mineralPrice():Int = {
    if (isUnit) {
      return unit.mineralPrice
    } else {
      return tech.mineralPrice
    }
  }

  def gasPrice():Int = {
    if (isUnit) {
      return unit.gasPrice
    } else {
      return tech.gasPrice
    }
  }

  def supplyRequired():Int = {
    if (isUnit) {
      return unit.supplyRequired
    } else {
      return 0
    }
  }

  override def toString: String = {
    if (isUnit) {
      return unit.toString
    } else {
      return tech.toString
    }
  }
}
