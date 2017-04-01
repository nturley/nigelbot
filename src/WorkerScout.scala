import bwapi.TilePosition

import scala.collection.JavaConverters._
import scala.collection.mutable



class WorkerScout {

  With.mapInfo.onDiscoverEnemyBuilding.subscribe(
    invoke = foundEnemy,
    label="WorkerScoutExploring2",
    priority = 2,
    oneShot = true
  )

  def foundEnemy(u:bwapi.Unit):Unit = {
    println("I FOUND THE ENEMY!")
    GameEvents.onFrame.unsubscribe("WorkerScoutExploring")
    unit.stop()
    With.miningManager.addUnit(name, unit, unit.getType)
    println("WorkerScout is done")
  }

  GameEvents.onFrame.subscribe(invoke=supplyTrigger,
    condition= _ => With.self.supplyUsed() >= 16,
    priority=0,
    label="WorkerScoutWait",
    oneShot=true)

  GameEvents.onFrame.subscribe(
    condition= _ => unit != null && candidates.nonEmpty && isInRegionState(candidates.head, RegionStatus.Unoccupied),
    invoke= _ => {
      candidates = startingLocsInRegionState(RegionStatus.Unexplored)
    },
    label="WorkerScoutExploring")

  GameEvents.onFrame.subscribe(
    invoke= _ => unit.move(candidates.head.toPosition),
    condition= _ => unit != null && unit.isIdle && candidates.nonEmpty,
    label="WorkerScoutExploring")

  var unit:bwapi.Unit = _
  var name:String = _
  var id:Int = _
  var candidates:mutable.Buffer[bwapi.TilePosition] = _

  def supplyTrigger(u:Null): Unit = {
    getUnit()
  }

  def isInRegionState(pos:bwapi.TilePosition, status:RegionStatus.RegionStatus):Boolean = {
    val name = With.names.getNameFor(bwta.BWTA.getRegion(pos))
    With.mapInfo.regionStatus(name) == status
  }

  def startingLocsInRegionState(status:RegionStatus.RegionStatus):mutable.Buffer[bwapi.TilePosition] = {
    for (loc:TilePosition <- With.game.getStartLocations.asScala if isInRegionState(loc, status)) yield loc
  }

  private def getUnit(): Unit = {
    candidates = startingLocsInRegionState(RegionStatus.Unexplored)
    name = With.miningManager.reassignMiner(candidates.head.toPosition)
    unit = With.names.getUnit(name)
    id = unit.getID
    unit.move(candidates.head.toPosition)
  }
}
