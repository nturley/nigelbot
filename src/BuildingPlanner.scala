import bwapi.{Color, TilePosition, UnitType}
import BuildDirection.BuildDirection
import scala.collection.JavaConverters._
import sun.reflect.generics.reflectiveObjects.NotImplementedException
/**
  * Created by Neil on 3/5/2017.
  */
class BuildingPlanner {

  val buildPlan = scala.collection.mutable.Queue.empty[BuildPosition]
  1 to 2 foreach { _ =>
    buildPlan.enqueue(new BuildPosition(BuildDirection.MAIN_BACK, 0))
  }
  1 to 8 foreach { _ =>
    buildPlan.enqueue(new BuildPosition(BuildDirection.MAIN_FRONT, 0))
  }
  1 to 10 foreach { _ =>
    buildPlan.enqueue(new BuildPosition(BuildDirection.MAIN_BACK, 0))
  }



  def getBuildTarget(building:UnitType): bwapi.Position = {
    val height = building.tileHeight()
    val width = building.tileWidth()

    if (building.isRefinery) {
      With.game.getNeutralUnits.asScala.foreach { (u: bwapi.Unit) =>
        if (u.getType == UnitType.Resource_Vespene_Geyser) {
          val reg  = With.names.getNameFor(bwta.BWTA.getRegion(u.getPosition))
          if (reg == With.scoutManager.myMainRegion) {
            return new bwapi.Position(u.getLeft, u.getTop)
          }
        }
      }
    }

    val buildDirective = buildPlan.head
    val dir = buildDirective.dir
    val margin = buildDirective.margin
    val (searchStart, reg) = if (dir == BuildDirection.MAIN_MIDDLE) {
      val reg = With.names.getRegion(With.scoutManager.myMainRegion)
      (reg.getCenter.toTilePosition, reg)
    } else if (dir == BuildDirection.MAIN_BACK) {
      val reg = With.names.getRegion(With.scoutManager.myMainRegion)
      assert(reg.getChokepoints.size() == 1)
      val chokeCenter = reg.getChokepoints.get(0).getCenter
      (furthestTileFrom(reg, chokeCenter), reg)
    } else if (dir == BuildDirection.MAIN_FRONT) {
      val reg = With.names.getRegion(With.scoutManager.myMainRegion)
      assert(reg.getChokepoints.size() == 1)
      val chokeCenter = reg.getChokepoints.get(0).getCenter
      (chokeCenter.toTilePosition, reg)
    } else {
        throw new NotImplementedException
    }
    val buildLoc = spiralSearch(searchStart, width, height, margin, reg)
    With.game.drawBoxMap(buildLoc.toPosition, new bwapi.TilePosition(buildLoc.getX+width, buildLoc.getY+height).toPosition, bwapi.Color.Green)
    With.game.drawBoxMap(new bwapi.TilePosition(buildLoc.getX-margin,buildLoc.getY-margin).toPosition, new bwapi.TilePosition(buildLoc.getX+width+margin, buildLoc.getY+height+margin).toPosition, bwapi.Color.Orange)
    return buildLoc.toPosition
  }

  // start at the center and travel away from the pos until you can't get any further
  def furthestTileFrom(reg:bwta.Region, pos:bwapi.Position) : bwapi.TilePosition = {
    val poly = reg.getPolygon
    val start = reg.getCenter
    val avoidTile = pos.toTilePosition
    val signX = if (start.getX > pos.getX) 1 else -1
    val signY = if (start.getY > pos.getY) 1 else -1

    var currPos = start.toTilePosition
    while (true) {
      val stepX = new TilePosition(currPos.getX + signX, currPos.getY)
      val stepY = new TilePosition(currPos.getX, currPos.getY + signY)
      val xOkay = stepX.isValid && poly.isInside(stepX.toPosition)
      val yOkay = stepY.isValid && poly.isInside(stepY.toPosition)
      if (!xOkay && !yOkay) return currPos
      if (!xOkay) {
        currPos = stepY
      } else if (!yOkay) {
        currPos = stepX
      } else {
        val xDist = stepX.getDistance(avoidTile)
        val yDist = stepY.getDistance(avoidTile)
        currPos = if (xDist>yDist) stepX else stepY
      }
    }
    throw new UnknownError("This shouldn't happen")
  }

  def spiralSearch(searchStart:TilePosition, width:Int, height:Int, margin:Int, reg:bwta.Region):TilePosition = {
    var dx = 1
    var dy = 0
    var x = searchStart.getX
    var y = searchStart.getY
    val poly = reg.getPolygon

    while (true) {
      val signX = if (dx != 0) dx/Math.abs(dx) else 0
      val signY = if (dy != 0) dy/Math.abs(dy) else 0
      //if (isBuildable(x, x+width, y, y+height, margin)) return searchStart
      if (dx==0) {
        y to y + dy by signY foreach{ py =>
          val topleft = new bwapi.TilePosition(x,py)
          val bottomRight = new bwapi.TilePosition(x+width, py + height)
          With.game.drawBoxMap(topleft.toPosition, bottomRight.toPosition, bwapi.Color.Green)
          if (poly.isInside(topleft.toPosition) && isBuildable(x, x+width, py, py+height, margin)) return topleft
        }
      } else {
        x to x + dx by signX foreach { px =>
          val topleft = new bwapi.TilePosition(px,y)
          val bottomRight = new bwapi.TilePosition(px+width, y + height)
          With.game.drawBoxMap(new bwapi.TilePosition(px,y).toPosition, new bwapi.TilePosition(px+width,y+height).toPosition, bwapi.Color.Green)
          if (poly.isInside(topleft.toPosition) && isBuildable(px, px+width, y, y+height, margin)) return new TilePosition(px, y)
        }
      }
      x+=dx
      y+=dy
      if (dx!=0) {
        dy = dx
        dx = 0
      } else {
        dx = -dy - signY
        dy = 0
      }
    }
    assert(false)
    return searchStart
  }

  def isBuildable(left:Int, right:Int, top:Int, bottom:Int, margin:Int):Boolean = {
    left - margin to right + margin-1 foreach { x =>
      top - margin to bottom + margin-1 foreach { y =>
        val tile = new TilePosition(x, y)
        if (!tile.isValid) return false
        if (!With.game.isBuildable(tile, true)) return false
      }
    }
    return true
  }
}
