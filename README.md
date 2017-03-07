# nigelbot
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
I got a kick out of the idea of tracking objects by assigning them a human memorable name. For instance units are named things like "Bob", "Alfonso", and "Ramon". And regions are named things like "Westchester" and "Newcastle". It's honestly probably pretty terrible for performance but it makes debugging way more fun. Instead of members and variables holding values like "Unit@24670", I see "Nigel". Building names a bit more boring. They are things like "Command Center A" and "Mineral Patch B1".

One of the other reasons this is a bad idea is that it makes a lot of code more verbose.

```
unit.move(position)
```
turns into 
```
With.names.getUnit(unit).move(position)
```

But so far, it's been more entertaining than irritating, so I've stuck with it.

Another reason I did it was because I was nervous about bwapi or BWMirror creating new instances of the same object so I wasn't confident I could compare references, so I needed some sort of unique identifier for units and regions.

## Overlays
I'm pretty proud of my overlay manager. The overlay manager can display lots of different information that can be toggled on and off by typing in the toggle name in the in-game chat. For instance, if you type "names", then all of the Unit names will become visible or invisible. In theory, if you are debugging a specific feature or subsystem then you can turn on relevant overlays to figure out what it's doing. Overlays should be designed so that they can all be visible simultaneously, (unit labels are all concatenated together instead of piling on top of each other)


