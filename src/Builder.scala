
/**
  * Created by Neil on 3/5/2017.
  */
class Builder (val builderName:String, val buildingType:bwapi.UnitType) {

  val target = With.buildingPlanner.getBuildTarget(buildingType)
  With.buildingPlanner.buildPlan.dequeue()
  var isBuilding = false

  def onFrame(): Unit = {
    val unit = With.names.getUnit(builderName)
    if (!isBuilding) {
      if (!unit.isMoving && unit.getDistance(target) > 50) {
        unit.move(target)
      }
      if (unit.getDistance(target) < 50) {
        unit.build(buildingType, target.toTilePosition)
        isBuilding = true
      }
    } else {
      // for protoss this happens for like one frame
      if (unit.getOrderTarget != null) {
        With.costManager.unreserve(buildingType)
        With.buildManager.releaseBuilder(this)
      }

    }
  }
}
