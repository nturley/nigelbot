import bwapi.{UnitType, UpgradeType}

import scala.collection.JavaConverters._
/**
  * Created by Neil on 3/5/2017.
  */
class BuildManager {

  val builders = scala.collection.mutable.HashSet.empty[Builder]

  val buildQueue = scala.collection.mutable.Queue.empty[Buildable]
  val probe = new Buildable(UnitType.Protoss_Probe)
  val pylon = new Buildable(UnitType.Protoss_Pylon)
  val forge = new Buildable(UnitType.Protoss_Forge)
  val cannon = new Buildable(UnitType.Protoss_Photon_Cannon)
  val shields = new Buildable(UpgradeType.Protoss_Plasma_Shields)
  val gateway = new Buildable(UnitType.Protoss_Gateway)
  1 to 4 foreach { _ =>
    buildQueue.enqueue(probe)
  }
  buildQueue.enqueue(pylon)
  buildQueue.enqueue(probe)
  buildQueue.enqueue(forge)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Assimilator))

  buildQueue.enqueue(pylon)
  1 to 2 foreach { _ =>
    buildQueue.enqueue(probe)
  }
  1 to 5 foreach { _ =>
    buildQueue.enqueue(cannon)
  }
  buildQueue.enqueue(shields)
  buildQueue.enqueue(pylon)
  buildQueue.enqueue(cannon)
  buildQueue.enqueue(cannon)


  def releaseBuilder(builder:Builder): Unit = {
    val name = builder.builderName
    val unit = With.names.getUnit(name)
    builders.remove(builder)
    With.miningManager.addUnit(name,unit, unit.getType)
  }

  def onFrame(): Unit = {
    builders.foreach(b => b.onFrame)

    if (buildQueue.size>0) {
      if (With.costManager.canAfford(buildQueue.head)) {
        val potentialBuild = buildQueue.head
        if (potentialBuild.isUnit) {
          val potentialUnit = potentialBuild.unit
          val whatBuilds = potentialUnit.whatBuilds().first
          if (whatBuilds.isBuilding) {
            val availableBuilding = With.self.getUnits.asScala.find((b: bwapi.Unit) =>
              b.getType == whatBuilds && !b.isTraining
            )
            if (availableBuilding.nonEmpty) {
              availableBuilding.get.train(buildQueue.dequeue.unit)
              return
            }
          } else if (whatBuilds.isWorker) {
            val buildingType = buildQueue.dequeue.unit
            With.costManager.reserve(buildingType)
            val minerName = With.miningManager.reassignMiner()
            builders.add(new Builder(minerName, buildingType))
            println(minerName + " has been reassigned to build a " + buildingType)
          } else {
            throw new NotImplementedError
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
