import scala.collection.mutable
import scala.collection.JavaConverters._
import RegionStatus._

/**
  * Created by Neil on 2/27/2017.
  */
class MapInfo {

  GameEvents.onFrame.subscribe(invoke=onFrame, label="MapInfo")

  GameEvents.onUnitDiscover.subscribe(invoke=foundEnemyBuilding,condition= u => {
    u.getPlayer.getID == With.game.enemy.getID && u.getType.isBuilding
  }, priority=100, label="MapInfo")

  val onDiscoverEnemyBuilding:Event[bwapi.Unit] = new Event[bwapi.Unit]()


  GameEvents.onUnitStatusChanged.subscribe(
    label="MapInfoFoundEnemyBuilding",
    invoke = (foundFriendlyBuilding _).tupled,
    condition = (uInfo:(String,bwapi.Unit,bwapi.UnitType)) => {
      val (name, unit, uType) = uInfo
      unit.getPlayer.getID == With.self.getID &&
      uType.isBuilding
    })

  private val startReg =bwta.BWTA.getRegion(With.self.getStartLocation)
  val myMainRegion:String = With.names.getNameFor(startReg)
  private val regs = startReg.getChokepoints.get(0).getRegions
  private val nat = if (regs.first == startReg) regs.second else regs.first
  val myNaturalRegion:String = With.names.getNameFor(nat)
  private val chokes = nat.getChokepoints
  val natChoke:bwta.Chokepoint = if (chokes.get(0) == startReg.getChokepoints.get(0)) chokes.get(1) else chokes.get(0)
  var firstEnemyRegion :String = _

  println("my main region: " + myMainRegion)
  println("my natural: " + myNaturalRegion)

  var regionStatus = mutable.HashMap.empty[String, RegionStatus]

  for (reg <- bwta.BWTA.getRegions.asScala) {
    val regName = With.names.getNameFor(reg)
    regionStatus(regName) = Unexplored
  }

  def foundFriendlyBuilding(name:String, u:bwapi.Unit, uType:bwapi.UnitType):Unit = {
    val name = With.names.getNameFor(bwta.BWTA.getRegion(u.getPosition))
    regionStatus(name) = FriendlyOccupy
  }

  def foundEnemyBuilding(u:bwapi.Unit):Unit = {
    println("Found Enemy Building")
    val name = With.names.getNameFor(bwta.BWTA.getRegion(u.getPosition))
    regionStatus(name) = FoeOccupy
    if (firstEnemyRegion == null) firstEnemyRegion = name
    onDiscoverEnemyBuilding.fire(u)
    println("MapInfo finished firing")
  }

  def onFrame(n:Null): Unit = {
    for ((regname, status) <- regionStatus) {
      if (status == Unexplored) {
        val reg = With.names.getRegion(regname)
        if (With.game.isExplored(reg.getCenter.toTilePosition)) {
          regionStatus(regname) = Unoccupied
        }
      }
    }
  }
}
