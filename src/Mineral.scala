import scala.collection.mutable

/**
  * Created by Neil on 2/26/2017.
  */
class Mineral (val name:String, unit:bwapi.Unit, val pos:bwapi.Position, val region:String) {
  val miners = mutable.HashSet.empty[Miner]
  var travelTime : Option[Int] = None
  var reservedFor : Int = 0
}
