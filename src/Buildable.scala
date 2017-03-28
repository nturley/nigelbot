class Buildable (val unit:bwapi.UnitType, val isUnit:Boolean,
                 val tech:bwapi.TechType, val isTech:Boolean,
                 val upgrade:bwapi.UpgradeType, val isUpgrade:Boolean,
                 val position:BuildPosition) {
  def this(unit:bwapi.UnitType, buildPosition: BuildPosition=null) = this(unit, true, null, false, null, false, buildPosition)
  def this(tech:bwapi.TechType) = this(null, false, tech, true, null, false, null)
  def this(upgrade:bwapi.UpgradeType) = this(null, false, null, false, upgrade, true, null)

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
