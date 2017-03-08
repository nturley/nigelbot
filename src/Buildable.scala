class Buildable (val unit:bwapi.UnitType, val isUnit:Boolean,
                 val tech:bwapi.TechType, val isTech:Boolean,
                 val upgrade:bwapi.UpgradeType, val isUpgrade:Boolean) {
  def this(unit:bwapi.UnitType) = this(unit, true, null, false, null, false)
  def this(tech:bwapi.TechType) = this(null, false, tech, true, null, false)
  def this(upgrade:bwapi.UpgradeType) = this(null, false, null, false, upgrade, true)

  def mineralPrice():Int = {
    if (isUnit) {
      return unit.mineralPrice
    } else if (isUpgrade) {
      return upgrade.mineralPrice
    } else {
      return tech.mineralPrice
    }
  }

  def gasPrice():Int = {
    if (isUnit) {
      return unit.gasPrice
    } else if (isTech) {
      return tech.gasPrice
    } else {
      return upgrade.gasPrice
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
    } else if (isTech) {
      return tech.toString
    } else {
      return upgrade.toString
    }
  }
}
