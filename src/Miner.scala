

class Miner (val unit:bwapi.Unit) {

  //GameEvents.onFrame.unsubscribe(With.names.getNameFor(unit) +" the Miner")
  GameEvents.onFrame.subscribe(invoke=minerOnFrame, label= With.names.getNameFor(unit) +" the Miner", priority = 1)

  val approxFramesToExtract = 80
  var target:Option[Mineral] = None
  var framesMining:Int = 0
  var lastFrameOrder:bwapi.Order = _

  def minerOnFrame(n:Null): Unit ={
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
    if (target.nonEmpty) target.get.miners.remove(this)
    target = None
  }

  def stopMining(): Unit = {
    clearTarget()
    GameEvents.onFrame.unsubscribe(With.names.getNameFor(unit) +" the Miner")
  }

  def assignNextTarget(): Unit = {
    val best = With.miningManager.findBestMineral(unit.getPosition)
    if (best != null) {
      target = Some(best)
      best.miners.add(this)
      best.reservedFor += approxFramesToExtract
    }
  }

}
