/**
  * Created by Neil on 2/27/2017.
  */
class Miner (val unit:bwapi.Unit) {
  val approxFramesToExtract = 80
  var target:Option[Mineral] = None
  var framesMining:Int = 0
  var lastFrameOrder:bwapi.Order = null
  def onFrame(): Unit ={
    val myOrder = unit.getOrder
    if (lastFrameOrder == null) lastFrameOrder = myOrder
    if (target.isEmpty) {
      assignNextTarget()
    }
    val orderTarget = unit.getOrderTarget
    val movingToMinerals = unit.getOrder == bwapi.Order.MoveToMinerals
    if (movingToMinerals) {
      framesMining = 0
    }
    if (myOrder == bwapi.Order.MoveToMinerals && lastFrameOrder != myOrder) {
      clearTarget()
      assignNextTarget()
    }
    if (unit.getOrder == bwapi.Order.MiningMinerals) {
      framesMining += 1
      if (framesMining <= approxFramesToExtract) {
        target.get.reservedFor -= 1
      }
    } else if (framesMining < approxFramesToExtract && framesMining > 0) {
      // I finished early, clear my reservation
      target.get.reservedFor -= approxFramesToExtract - framesMining
      framesMining = 0
    }
    if (orderTarget != null) {
      val targetName = With.names.getNameFor(orderTarget)
      val gatheringNonTarget = orderTarget != null && movingToMinerals && targetName != target.get.name
      if (gatheringNonTarget) {
        // Bad Miner! Bad!
        unit.gather(With.names.getUnit(target.get.name))
      }
    }
    if (unit.isIdle) {
      unit.gather(With.names.getUnit(target.get.name))
    }
    lastFrameOrder = unit.getOrder
  }

  def clearTarget(): Unit = {
    target.get.miners.remove(this)
    target = None
  }

  def assignNextTarget(): Unit = {
    val best = With.miningManager.findBestMineral(unit.getPosition)
    target = Some(best)
    best.miners.add(this)
    best.reservedFor += approxFramesToExtract
  }

}
