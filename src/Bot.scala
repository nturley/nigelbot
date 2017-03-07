import bwapi.{DefaultBWListener}
import bwta.BWTA

class Bot extends DefaultBWListener{
  override def onStart(): scala.Unit = {
    With.self = With.game.self()
    println("starting analysis...")
    BWTA.readMap()
    BWTA.analyze()
    println("analysis complete.")
    With.game.enableFlag(1)
    With.game.setLocalSpeed(20)
    With.names = new Names
    With.costManager = new CostManager
    With.miningManager = new MiningManager
    With.scoutManager = new ScoutManager
    With.buildManager = new BuildManager
    With.buildingPlanner = new BuildingPlanner
  }

  override def onFrame(): Unit = {
      Overlays.onFrame()
      With.miningManager.onFrame()
      With.scoutManager.onFrame()
      With.buildManager.onFrame()
  }

  def unitStatusChange(unit: bwapi.Unit): Unit = {
    val name = With.names.getNameFor(unit)
    if (unit.isCompleted &&
        unit.getPlayer.getID == With.self.getID &&
      (unit.getType.isWorker || unit.getType.isResourceDepot)) {
      With.miningManager.addUnit(name, unit, unit.getType)
    } else if (unit.getType.isMineralField) {
      With.miningManager.addUnit(name, unit, unit.getType)
    }
  }

  override def onUnitComplete(unit: bwapi.Unit): Unit = {
    unitStatusChange(unit)
  }

  override def onUnitCreate(unit: bwapi.Unit): Unit = {
    unitStatusChange(unit)
  }

  override def onUnitDiscover(unit: bwapi.Unit): Unit = {
    unitStatusChange(unit)
  }

  override def onUnitShow(unit: bwapi.Unit): Unit = {
    unitStatusChange(unit)
  }

  override def onUnitMorph(unit: bwapi.Unit): Unit = {
    unitStatusChange(unit)
  }

  override def onSendText(s: String): Unit = {
    Config.checkCommand(s)
  }
}
