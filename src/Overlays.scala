import bwapi.{Position, Text, UnitType}
import bwta.BWTA

import scala.collection.JavaConverters._

object Overlays {
  var frameNum = 0
  def onFrame(): Unit = {
    With.game.setTextSize(Text.Size.Enum.Small)

    BWTA.getRegions.asScala.foreach(region => {
      val regname = With.names.getNameFor(region)
      var label = ""
      if (Config.toggles("regions")) {
        _drawPolygonPositions(region.getPolygon.getPoints.asScala)
          label += regname
      }
      if (Config.toggles("scout")) {
        label += "\n(" + With.scoutManager.regionStatus(regname).toString() + ")"
      }
      if (Config.toggles("coords")) {
        label += "\n(" + region.getCenter.toString + ")"
      }
      With.game.setTextSize(Text.Size.Enum.Large)
      With.game.drawTextMap(region.getCenter, label)
      With.game.setTextSize(Text.Size.Enum.Small)
    })


    for (unit:bwapi.Unit <- With.game.getAllUnits.asScala) {
      if (With.names.hasName(unit) && unit.exists && unit.isCompleted) {
        var label = ""
        if (Config.toggles("names")) label += With.names.getNameFor(unit)
        if (Config.toggles("orders") && unit.getPlayer.getID == With.game.self().getID) label += "\n" + unit.getOrder.toString
        With.game.drawTextMap(unit.getLeft, unit.getBottom, label)
      }
    }

    if (Config.toggles("mining")) {
      With.game.drawBoxScreen(300, 0, 360, 20, bwapi.Color.Black, true)
      With.game.drawBoxScreen(300, 0, 360, 20, bwapi.Color.White)
      With.game.drawTextScreen(305, 5, "m/cf: " + With.miningManager.gatherRate.toString)
      for ((minerName, miner) <- With.miningManager.minersByName) {
        val unit = With.names.getUnit(minerName)
        val pos = unit.getPosition
        if (miner.target.nonEmpty) {
          val targetUnit = With.names.getUnit(miner.target.get.name)
          val targetPos = targetUnit.getPosition
          With.game.drawLineMap(pos, targetPos, bwapi.Color.White)
        }
      }
      for ((minName, mineral) <- With.miningManager.mineralsByName) {
        val unit = With.names.getUnit(minName)
        val pos = unit.getPosition
        With.game.drawCircleMap(pos, 10, bwapi.Color.Black, true)
        With.game.drawTextMap(pos.getX - 3, pos.getY - 5, mineral.miners.size.toString)
      }
      for (depot <- With.miningManager.depots) {
        val reg = bwta.BWTA.getRegion(depot.getPosition)
        val regName = With.names.getNameFor(reg)
        val minerals = With.miningManager.mineralsInRegion(regName)
        var miners = 0
        for (mineral <- minerals) {
          miners += mineral.miners.size
        }
        // honestly, it's a bit less than that
        // but that's definitely an upper bound
        val maxMiners = minerals.size * 3
        val pos = depot.getPosition
        With.game.drawCircleMap(pos, 10, bwapi.Color.Black, true)
        With.game.drawTextMap(pos.getX - 8, pos.getY - 5, miners + "/" + maxMiners)

      }
    }
    if (Config.toggles("toggles")) {
      With.game.drawBoxScreen(0, 0, 70, 145, bwapi.Color.Black, true)
      With.game.drawBoxScreen(0, 0, 70, 145, bwapi.Color.White)
      With.game.drawTextScreen(5, 5, Config.getCommands())
    }
    if (Config.toggles("buildQ")) {
      With.game.drawBoxScreen(70, 0, 140, 75, bwapi.Color.Black, true)
      With.game.drawBoxScreen(70, 0, 140, 75, bwapi.Color.White)
      With.game.drawTextScreen(75, 5, With.buildManager.buildQueueString)
    }
    if (Config.toggles("building")) {
      With.game.drawBoxScreen(140, 0, 210, 75, bwapi.Color.Black, true)
      With.game.drawBoxScreen(140, 0, 210, 75, bwapi.Color.White)
      With.game.drawTextScreen(145, 5, With.buildManager.buildingString)
      With.game.drawBoxScreen(210, 0, 280, 20, bwapi.Color.Black, true)
      With.game.drawBoxScreen(210, 0, 280, 20, bwapi.Color.White)
      With.game.drawTextScreen(215, 5, "resMin: " + With.costManager.reservedMinerals)
      for (builder <- With.buildManager.builders) {
        With.game.drawLineMap(builder.target, With.names.getUnit(builder.builderName).getPosition, bwapi.Color.Blue)
      }
      With.buildingPlanner.getBuildTarget(UnitType.Protoss_Pylon)
    }
    if (Config.toggles("frame")) {
      With.game.drawBoxScreen(360, 0, 420, 20, bwapi.Color.Black, true)
      With.game.drawBoxScreen(360, 0, 420, 20, bwapi.Color.White)
      With.game.drawTextScreen(365, 5, "frame: " + frameNum.toString)
    }
    frameNum += 1
  }
  def _drawPolygonPositions(points:Iterable[Position], color:bwapi.Color = bwapi.Color.Brown) {
    points.reduce((p1, p2) => { With.game.drawLineMap(p1, p2, color); p2 })
    With.game.drawLineMap(points.head, points.last, color)
  }
}
