class GaserManager {
  val managedNames = new scala.collection.mutable.HashSet[String]()
  val gasers = new scala.collection.mutable.HashSet[String]()
  GameEvents.onUnitStatusChanged.subscribe((addUnit _).tupled, (careAboutUnit _).tupled)
  def careAboutUnit(name:String, u:bwapi.Unit, uType:bwapi.UnitType): Boolean = {
    if (managedNames.contains(name)) return false
    if (!u.isCompleted) return false
    if (u.getPlayer.getID != With.self.getID) return false
    uType.isRefinery
  }

  def addUnit(name:String, u:bwapi.Unit, uType:bwapi.UnitType): Unit = {
    managedNames.add(name)
    for (_ <- 1 to 3) {
      val gasser = With.miningManager.reassignMiner()
      gasers += gasser
      With.names.getUnit(gasser).rightClick(u)
    }
  }

}
