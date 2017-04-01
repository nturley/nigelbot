

class CannonRush {
  var workerScout:WorkerScout = new WorkerScout
  var standbyWorker:ForwardStandbyWorker = new ForwardStandbyWorker

  With.mapInfo.onDiscoverEnemyBuilding.subscribe(
    invoke = building => {
      println("Cannon Rush found enemy building")
      assert(With.mapInfo.firstEnemyRegion != null)
      val buildQ = With.buildManager.buildQueue
      buildQ.clear()
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Pylon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Pylon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_OUT, 2)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Gateway,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_OUT, 1)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
      buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Gateway, BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_OUT, 1)))
      for (_ <- 1 to 50) {
        buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Probe))
        buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Pylon,BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
        buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Zealot))
        buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Zealot))
        buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Probe))
        for (_ <- 1 to 3) {
          buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Probe))
          buildQ.enqueue(new Buildable(bwapi.UnitType.Protoss_Photon_Cannon, BuildPosition(BuildDirection.ENEMY_MAIN_CHOKE_IN, 0)))
        }
      }
    },
    label="CannonRush",
    priority = 1,
    oneShot = true
  )

}
