import scala.collection.mutable
import scala.collection.JavaConverters._
/**
  * manages miners, depots, and minerals
  * locks new miners to a "best mineral"
  * removes miners from "worst mineral"
  */
class MiningManager {
  private val approxMinerSpeed = 3.0f
  private val managedNames = mutable.HashSet.empty[String]
  val mineralsInRegion = mutable.HashMap.empty[String, mutable.HashSet[Mineral]]

  var gatherRate = 0
  private var gatheredLastWindow = 0
  private var gatherWindowFrame = 0
  private val windowSize = 100

  for (reg <- With.names.getRegions()) {
    mineralsInRegion+=(reg -> new mutable.HashSet[Mineral]())
  }
  val mineralsByName = mutable.HashMap.empty[String, Mineral]
  val depotsInRegion = mutable.HashMap.empty[String, mutable.HashSet[String]]
  val depots = mutable.HashSet.empty[bwapi.Unit]
  val minersByName = mutable.HashMap.empty[String, Miner]

  def addUnit(name: String, unit:bwapi.Unit, uType:bwapi.UnitType): Unit = {
    if (managedNames.contains(name)) return
    managedNames.add(name)
    if (uType.isWorker) {
      minersByName += (name -> new Miner(unit))
    } else if (uType.isResourceDepot) {
      depots.add(unit)
    } else if (uType.isMineralField) {
      val pos = unit.getPosition
      val reg = bwta.BWTA.getRegion(pos)
      val regname = With.names.getNameFor(reg)
      val min = new Mineral(name, unit, pos, regname)
      mineralsInRegion(regname).add(min)
      mineralsByName += (name -> min)
    }
  }

  def onFrame(): Unit ={
    gatherWindowFrame += 1
    if (gatherWindowFrame == windowSize) {
      gatherRate = With.self.gatheredMinerals() - gatheredLastWindow
      gatheredLastWindow = With.self.gatheredMinerals()
      gatherWindowFrame = 0
    }
    for ((_, miner) <- minersByName) {
      miner.onFrame()
    }
  }

  def reassignMiner(): String = {
    val (name, miner) = minersByName.head
    minersByName.remove(name)
    managedNames.remove(name)
    miner.clearTarget()
    return name
  }

  def removeUnit(name: String, unit:bwapi.Unit, uType:bwapi.UnitType): Unit = {
    if (uType.isWorker) {
      minersByName.remove(name)
    } else if (uType.isResourceDepot) {
      val reg = bwta.BWTA.getRegion(unit.getPosition)
      val regname = With.names.getNameFor(reg)
      depotsInRegion(regname).remove(name)
    } else if (uType.isMineralField) {
      val reg = bwta.BWTA.getRegion(unit.getPosition)
      val regname = With.names.getNameFor(reg)
      val min = mineralsByName(name)
      mineralsInRegion(regname).remove(min)
      mineralsByName.remove(name)
    }
  }

  def findBestMineral(from:bwapi.Position): Mineral = {
    var bestMineral: Mineral = null
    var soonest = 0.0f
    val reg = With.names.getNameFor(bwta.BWTA.getRegion(from))
    for (min <- mineralsInRegion(reg)) {
      val dist = from.getApproxDistance(min.pos).toFloat
      val eta = dist / approxMinerSpeed
      val reservedFor = min.reservedFor
      val available = if (eta > reservedFor) eta else reservedFor
      if (bestMineral == null) {
        bestMineral = min
        soonest = available
      }
      if (available < soonest) {
        bestMineral = min
        soonest = available
      }
    }
    return bestMineral
  }
}
