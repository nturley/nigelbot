import bwapi.UnitType

/**
  * Created by Neil on 3/5/2017.
  */
class CostManager {
  var reservedSupply = 0
  var reservedMinerals = 0
  var reservedGas = 0

  def hasEnoughSupply(b:Buildable): Boolean = {
    return With.self.supplyTotal() - With.self.supplyUsed() - reservedSupply >= b.supplyRequired()
  }

  def hasEnoughMinerals(b:Buildable): Boolean = {
    return With.self.minerals() - reservedMinerals >= b.mineralPrice()
  }

  def hasEnoughGas(b:Buildable): Boolean = {
    return With.self.gas() - reservedGas >= b.gasPrice()
  }

  def canAfford(b:Buildable): Boolean = {
    return hasEnoughSupply(b) && hasEnoughGas(b) && hasEnoughMinerals(b)
  }

  def reserve(u:bwapi.UnitType): Unit = {
    reservedMinerals += u.mineralPrice()
    reservedGas += u.gasPrice()
    reservedSupply += u.supplyRequired()
  }

  def unreserve(u:bwapi.UnitType): Unit = {
    reservedMinerals -= u.mineralPrice()
    reservedGas -= u.gasPrice()
    reservedSupply -= u.supplyRequired()
  }


}
