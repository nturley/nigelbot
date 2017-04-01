import scala.collection.mutable.HashMap

object Config {
  GameEvents.onSendText.subscribe("CheckCommand",invoke=checkCommand)
  val toggles = HashMap.empty[String, Boolean]
  toggles+=("regions" -> true)
  toggles+=("toggles" -> true)
  toggles+=("names" -> false)
  toggles+=("mining" -> false)
  toggles+=("orders" -> false)
  toggles+=("scout" -> true)
  toggles+=("frame" -> false)
  toggles+=("buildQ" -> true)
  toggles+=("building" -> false)
  toggles+=("coords" -> false)
  toggles+=("onframe" -> true)



  def checkCommand(s:String) {
    val splut = s.split(" ")
    if (toggles.contains(splut(0))) {
      toggles(splut(0)) = !toggles(splut(0))
    }
  }

  def getCommands(): String = {
    var out = "Toggles: "
    for ((k,v) <- toggles) out += "\n" + k + ": " + v
    out
  }

}
