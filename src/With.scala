/**
  * Singleton to provide access to game-scoped classes
  */
object With {
  var game:bwapi.Game = null
  var self:bwapi.Player = null
  var names:Names = null
  var miningManager:MiningManager = null
  var scoutManager:ScoutManager = null
  var costManager:CostManager = null
  var buildManager:BuildManager = null
  var buildingPlanner:BuildingPlanner = null
}
