
object GameEvents {
  val onStart = new Event[bwapi.Game]()
  val onFrame = new Event[Null]()
  val onUnitComplete = new Event[bwapi.Unit]()
  val onUnitCreate = new Event[bwapi.Unit]()
  val onUnitDiscover = new Event[bwapi.Unit]()
  val onUnitShow = new Event[bwapi.Unit]()
  val onUnitMorph = new Event[bwapi.Unit]()
  val onUnitDestroy = new Event[bwapi.Unit]()
  val onUnitEvade = new Event[bwapi.Unit]()
  val onUnitHide = new Event[bwapi.Unit]()
  val onUnitRenegade = new Event[bwapi.Unit]()
  val onSendText = new Event[String]()
  val onEnd = new Event[Boolean]()
  val onPlayerLeft = new Event[bwapi.Player]()
  val onPlayerDropped = new Event[bwapi.Player]()
  val onReceiveText = new Event[(bwapi.Player, String)]()
  val onNukeDetect = new Event[bwapi.Position]()
  val onUnitStatusChanged = new Event[(String, bwapi.Unit, bwapi.UnitType)]()

  def unitStatusChange(u:bwapi.Unit): Unit = onUnitStatusChanged.fire(With.names.getNameFor(u), u, u.getType)
  onUnitCreate.subscribe("StatusChanged", invoke=unitStatusChange, priority = -100)
  onUnitComplete.subscribe("StatusChanged", invoke=unitStatusChange, priority = -100)
  onUnitDiscover.subscribe("StatusChanged", invoke=unitStatusChange, priority = -100)
  onUnitMorph.subscribe("StatusChanged", invoke=unitStatusChange, priority = -100)
  onUnitShow.subscribe("StatusChanged", invoke=unitStatusChange, priority = -100)

}
