This document is just to write down some architectural ideas that we were talking about and what I've been thinking about them.

UnitManagers
------------
1. All units have at most one UnitManager that controls their orders and movement
a. although there can be UnitManagers that manage other UnitManagers
2. UnitManagers can add and remove units

I think there needs to be some idea of unit requests

UnitRequests
------------
1. Unit requests have a priority
2. Unit requests have a minimum requirements function to decide if a specific unit is suitable
3. Unit requests have a preference function to decide between multiple suitable units

I guess unit managers register their list of requests and all unit managers try to fill those requests by transferring units.

I'm thinking every resource depot will have a MiningManager. The MiningManager will set up requests for workers at a lowish priority. Perhaps the priority gets lower and lower the more workers MiningManager is already managing. If there are two resource depots and one has more SCVs mining than the other then the priority of the worker request may rise to the level of transferring workers from one miningmanager to the other.

I'm thinking that each UnitManager gets to decide at what priority level to respond to requests. Maybe there should be some IdleUnitManager that collects all units that don't have anything to do and responds to even the lowest priority requests.

Unit Requests for Build Order Planning
--------------------------------------
I also like the idea of the unit requests being used for build order planning. Instead of simply filling requests with what we currently have, we can fill requests with units we may hypothetically have in the future.

To do this, I think you need UnitRequests to have some way to indicate how soon you need it. Or maybe that goes into the preference function (would you rather have a worker now, or a zealot in 100 frames?)

I'm guessing an example would look like this.

1. some military unitmanager decides it needs a zealot and registers a request
2. All of the other unit managers determine that none of their units are suitable
3. Some build subsystem requests a gateway to train the zealot
4. all of the unit managers determine that none of their units are suitable
5. Some build subsystem requests a worker to build the gateway
6. When it reaches the head of the buildqueue, the buildManager registers a request for a worker
7. The mining manager fills that request
8. the build manager takes that worker and starts construction on a gateway
9. once it's complete, there is still a pending zealot request, so the gateway begins training one
10. once it's complete, the military unit manager finally gets it's zealot

Requests as a Decision-Making model
-----------------------------------
The more general pattern here is that a subsystem makes some sort of prioritized request and other subsystems try to break down that request into the requirements needed to complete that request.

This model looks similar what PurpleWave was sorta doing with it's hiearchical planning but this one is a bit more ends-means.

In theory we could break down all decision-making into this sort of request framework.

* I need zealots -> 
* I need minerals to build zealots -> 
* I need miners to get minerals ->
* I need to build more miners

That way, the different build orders and strategies would hinge on the little variables that tweak the different priorities.

If you want a worker rushbot, then some military manager needs to request any unit ASAP with the highest priority. If you want a 4pool, then you lower the request priority to be below the miners so you immediately send all lings.

Ive been thinking about build order solving and I think its easiest to solve greedily. Instead of solving how many miners are optimal, you can compare how long its expected to take to reach your next goal without building any more miners and how long it will take if you build one miner first. For large mineral requests, adding more miners should speed it up until you hit a point when it starts to slow it down.

Finding the right number of gas miners can probably be done in a similar way. If it improves how fast we reach a goal we transfer a miner to gas.
