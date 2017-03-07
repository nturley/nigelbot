import scala.collection.mutable
import scala.collection.JavaConverters._
/**
  * Created by Neil on 2/27/2017.
  */
class ScoutManager {
  private val startReg =bwta.BWTA.getRegion(With.self.getStartLocation)
  val myMainRegion:String = With.names.getNameFor(startReg)
  val regs = startReg.getChokepoints.get(0).getRegions
  val nat = if (regs.first == startReg) regs.second else regs.first
  val myNaturalRegion:String = With.names.getNameFor(nat)
  println("my main region: " + myMainRegion)
  println("my natural: " + myNaturalRegion)

  object RegionStatus extends Enumeration {
    type RegionStatus = Value
    val Unexplored, Unoccupied, FriendlyOccupy, FoeOccupy = Value
  }
  import RegionStatus._
  var regionStatus = mutable.HashMap.empty[String, RegionStatus]

  for (reg <- bwta.BWTA.getRegions.asScala) {
    val regName = With.names.getNameFor(reg)
    regionStatus(regName) = Unexplored
  }

  def onFrame(): Unit = {
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
