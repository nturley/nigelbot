# Ideas of projects to work on

## gitignore
We need to add a gitignore file to make it easier to avoid checking in binaries or that workspace.xml file or something.

## Path finding
What is the shortest sequence of chokepoints to travel through to reach a specific bwta region? What is the shortest path between two points in the same bwta region?

## Walling analysis
Can this unit travel to this point? If I place this building type here, will this unit still be able to travel to this point? What is the best position for these three buildings to minimize the gap distance on this chokepoint?

## Improve Mining Manager
Find ways to increase worker mining speed. Also find a formula to predict a hypothetical mining rate for a given set of mineral positions, number of workers, etc.

## CI
It's probably too much to ask to set up a starcraft server to do runtime tests on our code, but we could do a simple compile test to confirm we don't break the build. I'm not certain how nicely something like travis-ci plays with intellij projects.

## AutoObserver
At some point, it will become convenient to have the camera automatically track the most interesting thing on the map so its less work to watch the bot play.

## Worker harrassment
One of the significant milestones in a BWAPI AI is beating the builtin AI. One of the easiest ways to beat the builtin AI is to send an early worker scout out to harrass their main base. The builtin AI can get distracted and you can bring their economy to a standstill. Many lower tier AI's are similarly vulnerable to simple harrassment, but very few bots seem to be doing advanced harrassment like clever cannon rush tactics or mining the backside of the mineral line.

## Defend against worker harrassment
After implementing harrassing our opponents workers, we will have to defend against harrassment against our own. This should also include ling rushes. A 4 pool rushbot is like the Hello World of bwapi bots.

## BWEM
BWTA2 has some irritating problems. I'd rather use BWEM. dgant is working on writing a JNI wrapper. We could help with that.

## Build management improvements
The build manager should use the build planner to speculatively plan out the positions of all of the buildings in the build queue.

We will also need a decision-making layer to generate items in the build queue. UAlberta uses this thing called BOSS to search for an optimal build order. We could either figure out a way to use it or write something that does roughly the same thing.

## Threat analysis
This could use potential fields (ala Ironbot, or Berkeley Overmind) or combat simulation (ala UAlbertaBot). Combat simulation seems like the most accurate approach, but it seems complicated and computationally expensive.

## Builder improvements
builders get stuck or confused all the time. The builder should detect why a builder hasn't started building and try to fix it. Perhaps eventually give up and reassign back to the mining manager and enqueue your building type back into the build queue.

# Recently completed

* Gitter chat room is up!
* Basic base building planning is now possible

# In Work

## Building Planner

build planner is usable but needs to 
