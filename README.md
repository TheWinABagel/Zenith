# Zenith

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
./gradlew.bat build
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
