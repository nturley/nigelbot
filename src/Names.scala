import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Created by Neil on 2/26/2017.
  */
class Names {
  private val unitID2Names = scala.collection.mutable.HashMap[Int, String]()
  private val name2ID = scala.collection.mutable.HashMap[String, Int]()
  private val coordString2name = scala.collection.mutable.HashMap[String, String]()
  for (reg <- bwta.BWTA.getRegions.asScala) {
    nameMe(reg)
  }
  val usedNames = scala.collection.mutable.HashSet[String]()
  var currentPerson = 0

  def hasName(unit:bwapi.Unit) : Boolean = {
    return unitID2Names.contains(unit.getID)
  }

  def getNameFor(unit:bwapi.Unit) : String = {
    val id = unit.getID
    if (!unitID2Names.contains(id)) nameMe(unit)
    return unitID2Names(id)
  }

  def getUnit(name:String) : bwapi.Unit = {
    if (name2ID.contains(name)) With.game.getUnit(name2ID(name))
    else {
      println("I don't know any units by the name: " + name)
      throw new IllegalArgumentException("unknown name: " + name)
    }
  }

  def getRegion(name:String) : bwta.Region = {
    for (reg <- bwta.BWTA.getRegions.asScala) {
      if (getNameFor(reg) == name) {
        return reg
      }
    }
    assert(false)
    return null
  }

  def nameMe(unit:bwapi.Unit) : Unit = {
    var ret = ""
    if (unit.getType.isBuilding ||
      unit.getType.isNeutral ||
      unit.getType.isSpell) {
      ret = findUnusedName(unit)
      usedNames.add(ret)
    } else {
      val size = NameLists.personNames.size
      ret = NameLists.personNames(currentPerson % size) + " " + NameLists.personSuffixes(currentPerson/size)
      currentPerson += 1
    }
    ret = ret.trim
    unitID2Names += (unit.getID -> ret)
    name2ID += (ret -> unit.getID)
  }

  def getNameFor(place:bwta.Region) : String = {
    val coordString = place.getCenter.toString
    return coordString2name(coordString)
  }

  def getRegions() : Iterable[String] = {
    return coordString2name.asJava.values().asScala
  }

  def findUnusedName(unit:bwapi.Unit): String = {
    val nameList = unit.getType.toString.split("_")

    val typename = nameList.slice(1, nameList.size).mkString(" ")

    for (suffix <- NameLists.otherSuffixes) {
      val potentialName:String = typename + " " + suffix
      if (!usedNames.contains(potentialName)) {
        return potentialName
      }
    }
    var i = 0;
    while (true) {
      for (suffix <- NameLists.otherSuffixes) {
        val potentialName: String = typename + " " + suffix + i
        if (!usedNames.contains(potentialName)) {
          return potentialName
        }
      }
      i += 1
    }
    return "????"
  }

  def nameMe(place:bwta.Region) : Unit = {
    val indexName = place.getCenter.toString
    var ret = NameLists.placeNames(coordString2name.size % NameLists.placeNames.size)
    if (NameLists.placeNames.size < coordString2name.size) {
      ret = "New " + ret
    }
    coordString2name += (indexName -> ret)
  }
}
