import bwapi.{BWEventListener, Player}

/**
  * Filters out some of the listener noise before passing it to GameEvents
  */
object Listener extends BWEventListener{
  val mirror:bwapi.Mirror = new bwapi.Mirror()
  var initializer = new Initializer()

  def initialize(): Unit = {
    mirror.getModule.setEventListener(this)
    mirror.startGame()
  }

  // game start/finish
  override def onStart(): Unit = {
    try {
      GameEvents.onStart.fire(mirror.getGame)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
  override def onEnd(b: Boolean): Unit = { GameEvents.onEnd.fire(b) }

  override def onFrame(): Unit = {
    try {
      GameEvents.onFrame.fire(null)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }



  // in game chat
  override def onSendText(s: String):                     Unit = { GameEvents.onSendText.fire(s) }
  override def onReceiveText(player: Player, s: String):  Unit = { GameEvents.onReceiveText.fire(player, s) }

  // network issues
  override def onPlayerLeft(player: Player):              Unit = { GameEvents.onPlayerLeft.fire(player) }
  override def onPlayerDropped(player: Player):           Unit = { GameEvents.onPlayerDropped.fire(player) }

  // nuke notification
  override def onNukeDetect(position: bwapi.Position):    Unit = { GameEvents.onNukeDetect.fire(position) }

  // units being created destroyed
  override def onUnitCreate(unit: bwapi.Unit):            Unit = { GameEvents.onUnitCreate.fire(unit) }
  override def onUnitDestroy(unit: bwapi.Unit):           Unit = { GameEvents.onUnitDestroy.fire(unit) }

  // unit state changes
  override def onUnitComplete(unit: bwapi.Unit):          Unit = { GameEvents.onUnitComplete.fire(unit) }
  override def onUnitMorph(unit: bwapi.Unit):             Unit = { GameEvents.onUnitMorph.fire(unit) }
  override def onUnitRenegade(unit: bwapi.Unit):          Unit = { GameEvents.onUnitRenegade.fire(unit) }

  // moving into and out of fog of war
  override def onUnitDiscover(unit: bwapi.Unit):          Unit = { GameEvents.onUnitDiscover.fire(unit) }
  override def onUnitEvade(unit: bwapi.Unit):             Unit = { GameEvents.onUnitEvade.fire(unit) }

  // invisible units
  override def onUnitHide(unit: bwapi.Unit):              Unit = { GameEvents.onUnitHide.fire(unit) }
  override def onUnitShow(unit: bwapi.Unit):              Unit = { GameEvents.onUnitShow.fire(unit) }

  override def onSaveGame(s: String):Unit = {}

}
