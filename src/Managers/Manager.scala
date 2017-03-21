package Managers

/**
  * Created by Zach Brown on 3/7/2017.
  * The abstract concept of a manager
  * I like OOP :)
  * Also it does seem like there will be a lot of these,
  * and they should have an interface that's well known and documented.
  */

trait Manager {
  // Every manager knows how to do their stuff during a frame
  def onFrame()
}

trait UnitManager extends Manager {
  // Unit managers have some units they are in control of
  def addUnit(name: String, unit:bwapi.Unit, uType:bwapi.UnitType)
  def removeUnit(name: String, unit:bwapi.Unit, uType:bwapi.UnitType)
  // TODO better name
  def nominateEmployeeForTransfer(requestedType:bwapi.UnitType): String
}
