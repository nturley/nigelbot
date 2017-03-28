[![Join the chat at https://gitter.im/nigelbot/Lobby](https://badges.gitter.im/nigelbot/Lobby.svg)](https://gitter.im/nigelbot/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
# nigelbot

Nigelbot is a broodwar bot written in Scala using the Java BWMirror language bindings for BWAPI. Right now, I'm leaning toward making it play protoss but I haven't gotten too far yet, so in theory it shouldn't take much to change it to something else.

## Installation
If you already did the setup for a Java-based bot, I think you should be able to open up the IntelliJ project, point it at your jdk and run it. Looks like I'm using version 2016.3.4 of IntelliJ.

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
I haven't gotten too far, but the pattern that seems to be emerging is that there are unit managers that subscribe to
game events to keep track of the units they have been assigned.

### Observations
One of the tricky bits about managing units are things like "my unit that was ordered to do something never actually did it, so subsystems are stuck waiting for it to complete", or "a condition triggers a unit to solve a problem and too many units all try to solve it at once". These sorts of problems seem to improve when you

1. create a manager class instance that manages progress toward completing a task
2. modify all game state planning to account for this task eventually starting and completing (the assigned units, the occupied space, the minerals, the problem solved, etc)
3. Use the manager to detect when progress is not being made or when something unexpectedly causes a delay (unit dies, is attacked, is idle before it finishes, etc) and try to fix the problem (try again, release the old unit and get a new one), or notify the parent manager so they can deal with resolving this issue.
4. once the task has completed, notify any interested managers and release any claimed units or resources

Its difficult to predict what type of delay or obstacles can come up in accomplishing a task, until you see it happen, but in theory, the resolution is usually simply monitoring the progress very carefully and picking the correct response to resolving it. Adding some basic sanity checks to managers to notice unusual behavior can really help with this.
