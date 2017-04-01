

/**
  * Singleton to provide access to game-scoped classes
  */
object With {
  var game:bwapi.Game = _
  var self:bwapi.Player = _
  var names:Names = _
  var miningManager:MiningManager = _
  var mapInfo:MapInfo = _
  var costManager:CostManager = _
  var buildManager:BuildManager = _
  var buildingPlanner:BuildingPlacer = _
  var gasserManager:GasserManager = _
  var strategy:CannonRush = _
}
