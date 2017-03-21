import Managers.UnitManager
import UnitEventHandlers.UnitEventHandlers.UnitEventHandler
import bwapi.DefaultBWListener
import bwta.BWTA

import scala.collection.mutable

class Bot extends DefaultBWListener{
  override def onStart(): scala.Unit = {
    With.self = With.game.self()
    println("starting analysis...")
    BWTA.readMap()
    BWTA.analyze()
    println("analysis complete.")
    With.game.enableFlag(1)
    With.game.setLocalSpeed(30)
    With.names = new Names
    With.costManager = new CostManager
    With.miningManager = new MiningManager
    With.scoutManager = new ScoutManager
    With.buildManager = new BuildManager
    With.buildingPlanner = new BuildingPlanner
    With.sparkyManager = new SparkyManager

    // With.unitToManagerHash = new mutable.HashMap[String, UnitManager]()
    //With.unitToHandlers = new mutable.HashMap[String, UnitEventHandler]()
  }

  override def onFrame(): Unit = {
      Overlays.onFrame()
      With.miningManager.onFrame()
      With.scoutManager.onFrame()
      With.buildManager.onFrame()
      With.sparkyManager.onFrame()
  }

  def unitStatusChange(unit: bwapi.Unit, event_name: String): Unit = {
    val name = With.names.getNameFor(unit)

    // If unit has an event handler for the event call it
    //if (With.unitToHandlers.contains(name)) {
    //    val handlerMap = With.unitToHandlers(name)
    //    if (handlerMap.contains(event_name)) {
    //        handlerMap(event_name)(unit)
    //        return
    //    }
    //}

    //if (unit.isCompleted &&
    //    unit.getPlayer.getID == With.self.getID &&
    //  (unit.getType.isWorker || unit.getType.isResourceDepot)) {
    //  With.miningManager.addUnit(name, unit, unit.getType)
    //} else if (unit.getType.isMineralField) {
    //  With.miningManager.addUnit(name, unit, unit.getType)
    //}
  }


  override def onUnitCreate(unit: bwapi.Unit): Unit = {
    //unitStatusChange(unit, "onUnitCreate")
  }

  override def onUnitComplete(unit: bwapi.Unit): Unit = {
    val name = With.names.getNameFor(unit)
    if (unit.getPlayer.getID == With.self.getID &&
        (unit.getType.isWorker || unit.getType.isResourceDepot))
            With.miningManager.addUnit(name, unit, unit.getType)
  }

  override def onUnitDestroy(unit: bwapi.Unit): Unit = {
  }

  override def onUnitDiscover(unit: bwapi.Unit): Unit = {
    val name = With.names.getNameFor(unit)
    if (unit.getType.isMineralField)
        With.miningManager.addUnit(name, unit, unit.getType)
  }

  override def onUnitEvade(unit: bwapi.Unit): Unit = {
  }

  override def onUnitShow(unit: bwapi.Unit): Unit = {
    unitStatusChange(unit, "onUnitShow" )
  }

  override def onUnitHide(unit: bwapi.Unit): Unit = {
  }

  override def onUnitMorph(unit: bwapi.Unit): Unit = {
  }

  override def onSendText(s: String): Unit = {
    Config.checkCommand(s)
  }
}
