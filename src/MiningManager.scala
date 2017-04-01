import scala.collection.mutable
import scala.collection.JavaConverters._
/**
  * manages miners, depots, and minerals
  * locks new miners to a "best mineral"
  * removes miners from "worst mineral"
  */
class MiningManager {

  GameEvents.onFrame.subscribe(invoke=onFrame, label="MiningManager")
  GameEvents.onUnitStatusChanged.subscribe("MiningManager", invoke=(addUnit _).tupled, condition=(careAboutUnit _).tupled)

  def careAboutUnit(name:String, u:bwapi.Unit, uType:bwapi.UnitType): Boolean = {
    if (managedNames.contains(name)) return false
    if (uType.isMineralField) return true
    if (!u.isCompleted) return false
    if (u.getPlayer.getID != With.self.getID) return false
    if (u.getType.isWorker || u.getType.isResourceDepot) return true
    false
  }

  private val approxMinerSpeed = 10.0f
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

  def addUnit(name:String, unit:bwapi.Unit, uType:bwapi.UnitType): Unit = {
    if (name==null) {
      println("Name is null!")
      throw new IllegalArgumentException("name is null")
    }
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

  def onFrame(n:Null): Unit ={
    gatherWindowFrame += 1
    if (gatherWindowFrame == windowSize) {
      gatherRate = With.self.gatheredMinerals() - gatheredLastWindow
      gatheredLastWindow = With.self.gatheredMinerals()
      gatherWindowFrame = 0
    }
  }

  def reassignMiner(near:bwapi.Position): String = {
    var nearest = Int.MaxValue
    var nearestMiner:(String, Miner) = null
    for ((name, miner) <- minersByName) {
      val d = miner.unit.getPosition.getApproxDistance(near)
      if (d < nearest) {
        nearest = d
        nearestMiner = (name, miner)
      }
    }
    minersByName.remove(nearestMiner._1)
    managedNames.remove(nearestMiner._1)
    nearestMiner._2.stopMining()
    nearestMiner._1
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
    bestMineral
  }
}
