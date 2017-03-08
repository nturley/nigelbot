# nigelbot

[![Join the chat at https://gitter.im/nigelbot/Lobby](https://badges.gitter.im/nigelbot/Lobby.svg)](https://gitter.im/nigelbot/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
Nigelbot is a broodwar bot written in Scala using the Java BWMirror language bindings for BWAPI. Right now, I'm leaning toward making it play protoss but I haven't gotten too far yet, so in theory it shouldn't take much to change it.

## Installation
If you already did the setup for a Java-based bot, I think you should be able to open up the IntelliJ project and run it. Looks like I'm 
using 2016.3.4.

## Status
It can mine minerals, train units from a build queue and it's just starting to figure out how to build a pylon and that's about it. Obviously it still has a ways to go.

## Organization
I used a lot of the structure from PurpleWave as inspiration, but all of the code is pretty much written from scratch. If I was smarter then I probably would've just built Nigelbot using PurpleWave as a base, but PurpleWave is complicated, so if I make mine from scratch I don't need to figure out how all of the complicated bits of PurpleWave work.

## Singletons
There's a lot of data that every class needs, but there is only one instance of it at any one time. That means that it's either a static class or a singleton. As an added wrinkle, whenever a new game starts we need to refresh all of this data.

PurpleWave used a Scala object (a singleton) to give everything access to game-wide data. We construct new instances of all of these members at the start of each match. Nigelbot follows the same pattern. This singleton class is called "With", so you'll see a lot of that in the code.

## Names
I got a kick out of the idea of tracking objects by assigning them a human memorable name. For instance units are named things like "Bob", "Alfonso", and "Ramon". And regions are named things like "Westchester" and "Newcastle". It's honestly probably pretty terrible for performance but it makes debugging way more fun. Instead of members and variables holding values like "Unit@24670", I see "Nigel". Building names are a bit more boring. They are things like "Command Center A" and "Mineral Patch B1".

One of the other reasons this is a bad idea is that it makes a lot of code more verbose.

```
unit.move(position)
```
turns into 
```
With.names.getUnit(unit).move(position)
```

But so far, it's been more entertaining than irritating, so I've stuck with it pretty consistently.

Another reason I did it was because I was nervous about bwapi or BWMirror creating new instances of the same object so I wasn't confident I could compare references, so I needed some sort of unique identifier for units and regions and why not making it a fun identifier instead of a boring one like an ID number?

## Overlays
I'm pretty proud of my overlay manager. The overlay manager can display lots of different information that can be toggled on and off by typing in the toggle name in the in-game chat. For instance, if you type "names", then all of the Unit names will become visible or invisible. In theory, if you are debugging a specific feature or subsystem then you can turn on relevant overlays to figure out what it's doing. Overlays should be designed so that they can all be visible simultaneously, (unit labels are all concatenated together instead of piling on top of each other)

## General Organizational Patterns
I haven't gotten too far, but so far, a pattern that is emerging is that Bot notifies a unit manager when a unit status changes that might be of interest to a particular manager by calling "addUnit" to that manager. The manager detects whether it cares about that unit or whether it is already tracking that unit and decides whether to add it to its indices and sometimes wraps them in a single-unit manager.

Every frame the manager goes through its indexed units and calls onFrame on their single-unit manager or just tells the unit what to do directly. Managers can transfer units to each other when they need them or no longer need them.

At first, I suspect its going to be a pretty flat collection of entities that tell each other what to do. For instance, the Building Manager is pretty much a sibling of the mining manager, whenever it needs a worker, it asks the mining manager for one. Similarly, I expect at some point we will have a base defender class that monitors whether enemy units are wandering into our territory and might ask the mining manager for workers if it needs them to drive off an enemy invader. We might have some classes that only monitor game state and others that only order units to follow whatever directives are given to them by other entities.

Ideally, once we have enough of these lower level managers, planners and monitors then we can start layering higher level reasoning on top of them that can guide the managers toward specific goals from a config file or by observations of the game state.

### Observations
One of the tricky bits about managing units are things like "my unit that was ordered to do something never actually did it, so subsystems are stuck waiting for it to complete", or "a condition triggers a unit to solve a problem and too many units all try to solve it at once". These sorts of problems seem to improve when you

1. create a manager class instance that manages progress toward completing a task
2. modify all game state planning to account for this task eventually starting and completing (the assigned units, the occupied space, the minerals, the problem solved, etc)
3. Use the manager to detect when progress is not being made or when something unexpectedly causes a delay (unit dies, is attacked, is idle before it finishes, etc) and try to fix the problem (try again, release the old unit and get a new one), or notify the parent manager so they can deal with resolving this issue.
4. once the task has completed, notify any interested managers and release any claimed units or resources

Its difficult to predict what type of delay or obstacles can come up in accomplishing a task, until you see it happen, but in theory, the resolution is usually simply monitoring the progress very carefully and picking the correct response to resolving it. Adding some basic sanity checks to managers to notice unusual behavior can really help with this.

## Mining Manager
Optimizing mining is super hard. The first level of optimization that I tried was locking workers to a mineral field. The next level I tried was that every time a worker returns a mineral load, they find the mineral field that they predict they can start mining the soonest and reserve it for about as long as it takes to mine a new load (~80 frames). Every frame they are actually mining they decrement the reservation period. A worker estimates about how many frames it will take to travel to the mineral field (which isn't a very accurate estimate) and takes that into account when picking its next target.

I also tried tracking the number of minerals gathered per 100 frames. The number isn't very stable. I should probably do some sort of low pass filter on it to get a more consistent reading on the current mining rate. Even with the instability of the number, it is already pretty apparent that there is pretty impressive diminishing returns on miners. I doubt it makes economic sense to go much above two per mineral field considering that it takes ~8 miners worth of minerals to build an expansion.

### Future work
Two biggest forms of inefficiency in the currrent algorithm is inaccuracy in the travel time estimate and neglecting the cost of the time it takes to return the minerals. There are also some mineral fields that are super fast to collect from because the worker can efficiently turn around without completely decelerating.

#### Historical travel time
In theory, the mineral can remember how long it takes a worker to travel between the command center and the mineral field and the miner can favor which minerals are efficient in practice instead of an inaccurate estimate. This would also allow the miners to adjust to things that obstruct the path between the resource depot, and prefer minerals that are the least obstructed.

#### More accurate travel estimates
There may be many interesting algorithms Nigelbot may be able to use if it could accurately predict exactly how long it will take to reach a spot on the map. This might be the first of many subsystems that could benefit from an accurate traveltime estimator.

#### Minimize deceleration
When miners approach a mineral field or the resource depot, they decelerate. If they are commanded to travel beyond the mineral field or resource depot until the last moment, then they can travel faster. If they are given non-mining order they lose collision exemption, so they risk slowing down to go around each other. So this will need to only be done selectively or a more complicated cooperative path finding algorithm will be needed.

## Build Manager
The build manager attempts to train, construct, or research whatever is in its build queue exactly in its specified order.

### Future work
I'm toying with the idea of a secondary list of buildables that is used whenever the first queue is empty. I'm thinking the secondary list would be used for building up specific army compositions so units in the secondary list would probably prioritize units that it has relatively few of.

## Build Planner
The build planner finds good positions for buildings. I think my vision is that whenever something populates the build manager with some buildings in its build queue it also tells the build planner where it wants them. The build planner should take high level placement directives like "spaced far apart in the back of my main" or "packed tight near my main choke point" and plan good placement decisions. Initial searching could involve a simple spiral search. Later this could involve path analysis to detect whether a potential building walls us off.

