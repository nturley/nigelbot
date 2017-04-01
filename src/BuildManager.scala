import bwapi.{TechType, UnitType, UpgradeType}

import scala.collection.JavaConverters._

class BuildManager {
  GameEvents.onFrame.subscribe("BuildManager",invoke=onFrame)

  val builders = scala.collection.mutable.HashSet.empty[Builder]
  val buildQueue = scala.collection.mutable.Queue.empty[Buildable]

  private val probe = new Buildable(UnitType.Protoss_Probe)
  private val pylonNatChoke = new Buildable(UnitType.Protoss_Pylon, BuildPosition(BuildDirection.NATURAL_CHOKE, 3))
  private val pylonMiddle = new Buildable(UnitType.Protoss_Pylon, BuildPosition(BuildDirection.MAIN_MIDDLE, 3))
  private val forge = new Buildable(UnitType.Protoss_Forge, BuildPosition(BuildDirection.MAIN_MIDDLE, 0))
  private val cannonNatChoke = new Buildable(UnitType.Protoss_Photon_Cannon, BuildPosition(BuildDirection.NATURAL_CHOKE, 0))
  for (_ <- 1 to 3) {
    buildQueue.enqueue(probe)
  }
  buildQueue.enqueue(pylonMiddle)
  buildQueue.enqueue(probe)
  buildQueue.enqueue(forge)
  for (_ <- 1 to 5) {
    buildQueue.enqueue(probe)
  }

  def releaseBuilder(builder:Builder): Unit = {
    val name = builder.builderName
    val unit = With.names.getUnit(name)
    builders.remove(builder)
    With.miningManager.addUnit(name, unit, unit.getType)
  }

  def onFrame(n:Null): Unit = {
    if (buildQueue.nonEmpty) {
      if (With.costManager.canAfford(buildQueue.head)) {
        val potentialBuild = buildQueue.head
        if (potentialBuild.isUnit) {
          val potentialUnit = potentialBuild.unit
          val whatBuilds = potentialUnit.whatBuilds().first
          if (!potentialUnit.isBuilding) {
            val availableTrainer = With.self.getUnits.asScala.find((b: bwapi.Unit) =>
              b.getType == whatBuilds && !b.isTraining && b.canTrain(potentialUnit)
            )
            if (availableTrainer.nonEmpty) {
              availableTrainer.get.train(buildQueue.dequeue.unit)
              return
            }
          } else {
            if (!With.self.getUnits.asScala.find(u => u.getType.isWorker).get.canBuild(potentialUnit)) return
            val buildingType = buildQueue.dequeue.unit
            With.costManager.reserve(buildingType)
            val (targetPos, reg) = With.buildingPlanner.buildPositionToPositionAndRegion(potentialBuild.position.dir)
            val minerName = With.miningManager.reassignMiner(targetPos)
            builders.add(new Builder(minerName, buildingType, potentialBuild.position))
          }
        } else if (potentialBuild.isUpgrade) {
          val availableUnit = With.self.getUnits.asScala.find((u:bwapi.Unit) =>
            u.canUpgrade(potentialBuild.upgrade) && !u.isUpgrading
          )
          if (availableUnit.nonEmpty) {
            availableUnit.get.upgrade(buildQueue.dequeue.upgrade)
            return
          }
        } else {
          throw new NotImplementedError
        }
      }
    }
  }

  def buildQueueString(): String = {
    var ret = "Build Q: (" + buildQueue.size+")\n"
    var i = 0
    for (b <- buildQueue) {
      ret += b.toString + "\n"
      if (i>3) return ret
      i+=1
    }
    return ret
  }

  def buildingString(): String = {
    var ret = "Building:\n"
    for (u:bwapi.Unit <- With.self.getUnits.asScala) {
      if (u.isTraining) {
        ret += u.getTrainingQueue.get(0).toString + "\n"
      }
    }
    return ret
  }

}
