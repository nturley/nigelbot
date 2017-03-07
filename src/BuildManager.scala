import bwapi.UnitType
import scala.collection.JavaConverters._
/**
  * Created by Neil on 3/5/2017.
  */
class BuildManager {

  val builders = scala.collection.mutable.HashSet.empty[Builder]

  val buildQueue = scala.collection.mutable.Queue.empty[Buildable]
  1 to 3 foreach { _ =>
    buildQueue.enqueue(new Buildable(UnitType.Protoss_Probe))
  }
  buildQueue.enqueue(new Buildable(UnitType.Protoss_Pylon))
  1 to 3 foreach { _ =>
    buildQueue.enqueue(new Buildable(UnitType.Protoss_Probe))
  }

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
