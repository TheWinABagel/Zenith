# Zenith

## This mod is pretty much in a (mostly) functional dev limbo. It's source is a total mess that I made while learning to program. I'm now a full time software dev, and the mere thought of working on Zenith is painful. There's simply nothing left for me to learn from it, which is my primary motivator for projects. Just bug whack-a-mole is left for me. 
With that being said, I would like to give a special thanks to Muon. You're the reason I even picked up this project. You kept telling me I should fix up this buggy mess of a mod because your server kept crashing because of it, and it led me to a path of becoming a professional programmer. Thank you for egging me on. (even when it got on my nerves :P)

https://www.curseforge.com/minecraft/mc-mods/zenith

Zenith is a mod about adding additional layers of content to vanilla systems. It doesn't add much in the way of blocks or items, but it adds a significant amount of content. It's divided into modules, each separate from another. Any module can be disabled at will, but all the modules work best when they are all enabled.

Zenith is an unofficial fabric port of Apotheosis. The forge version can be found [here](https://www.curseforge.com/minecraft/mc-mods/apotheosis).

## Setup

### Install Gradle

Install the latest 8.x release of [Gradle](https://gradle.org/install/).

### Setup Local Dependencies 

Zenith requires both [FakerLib](https://github.com/TheWinABagel/FakerLib) and [ZenithAttributes](https://github.com/TheWinABagel/ZenithAttributes) to build.

For each of these dependencies, you must clone the repository and then publish it to `mavenLocal`:

```
./gradlew.bat publishToMavenLocal
```

> NOTE: The versions of these dependencies that you have cloned locally should match the versions defined in `gradle.properties`.

### Build Jar

From here, you should be able to build Zenith:

```
./gradlew.bat build
```

## Credits

- **Shadows of Fire**: Creator of Apotheosis and the forge maintainer
- **Faellynna**: Artist of Apotheosis

## License

This mod is licensed under the MIT License
