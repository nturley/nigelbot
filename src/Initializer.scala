
import bwta.BWTA

/**
  * Created by Neil on 3/26/2017.
  */
class Initializer {
  def initialize(g:bwapi.Game): Unit = {
    With.game = g
    With.self = g.self()
    println("starting analysis...")
    BWTA.readMap()
    BWTA.analyze()
    println("analysis complete.")
    g.enableFlag(1)
    g.setLocalSpeed(20)
    With.names = new Names
    With.costManager = new CostManager
    With.miningManager = new MiningManager
    With.mapInfo = new MapInfo
    With.buildManager = new BuildManager
    With.buildingPlanner = new BuildingPlacer
    With.gasserManager = new GasserManager

    With.strategy = new CannonRush

    // objects don't seem to like subscribing as much as class instances
    GameEvents.onFrame.subscribe(invoke=Overlays.onFrame, label= "Overlays", priority=100)
  }
  GameEvents.onStart.subscribe("Initialize",invoke=initialize, priority = 100)
}
