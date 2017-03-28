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
    With.scoutManager = new ScoutManager
    With.buildManager = new BuildManager
    With.buildingPlanner = new BuildingPlacer
    With.gaserManager = new GaserManager
    GameEvents.onFrame.subscribe(Overlays.onFrame)
  }
  GameEvents.onStart.subscribe(initialize, priority = Int.MaxValue)
}
