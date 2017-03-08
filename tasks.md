# Ideas of projects to work on

## gitignore
We need to add a gitignore file to make it easier to avoid checking in binaries or that workspace.xml file or something.

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
We will need more sophisticated build planning system. UAlberta uses this thing called BOSS to search for an optimal build order. We could either figure out a way to use it or write something that does roughly the same thing. This could feed the build manager's build queue.

## Combat estimator
At some point we are going to need to do threat analysis. That could mean finding a way to wrap Sparcraft from UAlbertaBot or rolling our own. The main goal is to determine how dangerous a collection of enemy units is to a collection of friendly units.

# Recently completed

* Gitter chat room is up!
* Spiral search looks correct

# In Work

## Building Planner

Build planner probably needs some finishing up, notably supporting other locations besides middle of main
