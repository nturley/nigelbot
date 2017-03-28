
/**
  * Created by Neil on 3/5/2017.
  */
class Builder (val builderName:String, val buildingType:bwapi.UnitType, val buildPosition: BuildPosition) {

  var target = With.buildingPlanner.getBuildTarget(buildingType, buildPosition)
  var isBuilding = false
  private var buildWait = 0

  def onFrame(): Unit = {
    val unit = With.names.getUnit(builderName)
    if (!isBuilding || buildWait > 100) {
      if (!unit.isMoving && unit.getDistance(target) > 150) {
        unit.move(target)
      }
      if (unit.getDistance(target) < 150) {
        if (unit.canBuild(buildingType, target.toTilePosition)) {
          unit.build(buildingType, target.toTilePosition)
          isBuilding = true
          buildWait = 0
        } else {
          target = With.buildingPlanner.getBuildTarget(buildingType, buildPosition)
        }
      }
    } else {
      // for protoss this happens for like one frame
      if (unit.getOrderTarget != null) {
        With.costManager.unreserve(buildingType)
        With.buildManager.releaseBuilder(this)
      } else {
        buildWait += 1
      }

    }
  }
}
