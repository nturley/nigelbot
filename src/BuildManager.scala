import bwapi.{TechType, UnitType, UpgradeType}

import scala.collection.JavaConverters._
/**
  * Created by Neil on 3/5/2017.
  */
class BuildManager {
  GameEvents.onFrame.subscribe(onFrame)

  val builders = scala.collection.mutable.HashSet.empty[Builder]

  val buildQueue = scala.collection.mutable.Queue.empty[Buildable]
  val probe = new Buildable(UnitType.Protoss_Probe)
  val pylonBack = new Buildable(UnitType.Protoss_Pylon, new BuildPosition(BuildDirection.MAIN_BACK, 0))
  val pylonFront = new Buildable(UnitType.Protoss_Pylon, new BuildPosition(BuildDirection.MAIN_FRONT, 0))
  val forge = new Buildable(UnitType.Protoss_Forge, new BuildPosition(BuildDirection.MAIN_BACK, 0))
  val cannon = new Buildable(UnitType.Protoss_Photon_Cannon, new BuildPosition(BuildDirection.MAIN_FRONT, 0))
  val shields = new Buildable(UpgradeType.Protoss_Plasma_Shields)
  val gateway = new Buildable(UnitType.Protoss_Gateway, new BuildPosition(BuildDirection.MAIN_BACK, 0))
  val carrier = new Buildable(UnitType.Protoss_Carrier)
  1 to 4 foreach { _ =>
    buildQueue.enqueue(probe)
  }
  buildQueue.enqueue(pylonBack)
  buildQueue.enqueue(probe)
  buildQueue.enqueue(forge)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Assimilator))

  buildQueue.enqueue(pylonFront)
  1 to 2 foreach { _ =>
    buildQueue.enqueue(probe)
  }
  1 to 5 foreach { _ =>
    buildQueue.enqueue(cannon)
  }
  buildQueue.enqueue(shields)
  buildQueue.enqueue(pylonFront)
  1 to 3 foreach { _ =>
    buildQueue.enqueue(probe)
  }
  buildQueue.enqueue(cannon)
  buildQueue.enqueue(cannon)
  buildQueue.enqueue(gateway)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Cybernetics_Core, new BuildPosition(BuildDirection.MAIN_BACK, 0)))
  buildQueue.enqueue(pylonBack)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Stargate, new BuildPosition(BuildDirection.MAIN_BACK, 0)))
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Fleet_Beacon, new BuildPosition(BuildDirection.MAIN_BACK, 0)))
  buildQueue.enqueue(carrier)
  buildQueue.enqueue(pylonBack)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(new Buildable(UpgradeType.Carrier_Capacity))
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(new Buildable(UpgradeType.Protoss_Air_Armor))
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(pylonBack)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(new Buildable(UpgradeType.Protoss_Air_Weapons))
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  1 to 4 foreach { _ =>
    buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  }
  buildQueue.enqueue(pylonBack)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  1 to 4 foreach { _ =>
    buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  }
  buildQueue.enqueue(pylonBack)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  buildQueue.enqueue(carrier)
  1 to 100 foreach { _ =>
    buildQueue.enqueue(new Buildable(UnitType.Protoss_Interceptor))
  }

  def releaseBuilder(builder:Builder): Unit = {
    val name = builder.builderName
    val unit = With.names.getUnit(name)
    builders.remove(builder)
    With.miningManager.addUnit(name,unit, unit.getType)
  }

  def onFrame(n:Null): Unit = {
    builders.foreach(b => b.onFrame)

    if (buildQueue.size>0) {
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
            val minerName = With.miningManager.reassignMiner()
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
