import bwapi.{Color, TilePosition, UnitType}
import BuildDirection.BuildDirection
import sun.reflect.generics.reflectiveObjects.NotImplementedException
/**
  * Created by Neil on 3/5/2017.
  */
class BuildingPlanner {

  val buildPlan = scala.collection.mutable.Queue.empty[BuildPosition]
  buildPlan.enqueue(new BuildPosition(BuildDirection.MAIN_MIDDLE, 2))

  def getBuildTarget(building:UnitType): bwapi.Position = {
    val height = building.tileHeight()
    val width = building.tileWidth()
    val buildDirective = buildPlan.head
    val dir = buildDirective.dir
    val margin = buildDirective.margin
    if (dir==BuildDirection.MAIN_MIDDLE) {
      val searchStart = With.names.getRegion(With.scoutManager.myMainRegion).getCenter.toTilePosition
      val buildLoc = spiralSearch(searchStart, width, height, margin)

      With.game.drawBoxMap(buildLoc.toPosition, new bwapi.TilePosition(buildLoc.getX+width, buildLoc.getY+height).toPosition, bwapi.Color.Green)
      With.game.drawBoxMap(new bwapi.TilePosition(buildLoc.getX-margin,buildLoc.getY-margin).toPosition, new bwapi.TilePosition(buildLoc.getX+width+margin, buildLoc.getY+height+margin).toPosition, bwapi.Color.Orange)

      return buildLoc.toPosition
    }
    throw new NotImplementedException
    //return With.names.getRegion(With.scoutManager.myMainRegion).getCenter
  }

  def spiralSearch(searchStart:TilePosition, width:Int, height:Int, margin:Int):TilePosition = {
    var dx = 1
    var dy = 0
    var x = searchStart.getX
    var y = searchStart.getY

    while (true) {
      val signX = if (dx != 0) dx/Math.abs(dx) else 0
      val signY = if (dy != 0) dy/Math.abs(dy) else 0
      //if (isBuildable(x, x+width, y, y+height, margin)) return searchStart
      if (dx==0) {
        y to y + dy by signY foreach{ py =>
          With.game.drawBoxMap(new bwapi.TilePosition(x,py).toPosition, new bwapi.TilePosition(x+width,py+height).toPosition, bwapi.Color.Green)
          if (isBuildable(x, x+width, py, py+height, margin)) return new TilePosition(x, py)
        }
      } else {
        x to x + dx by signX foreach { px =>
          With.game.drawBoxMap(new bwapi.TilePosition(px,y).toPosition, new bwapi.TilePosition(px+width,y+height).toPosition, bwapi.Color.Green)
          if (isBuildable(px, px+width, y, y+height, margin)) return new TilePosition(px, y)
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
        if (!With.game.isBuildable(new TilePosition(x, y), true)) return false
      }
    }
    return true
  }
}
