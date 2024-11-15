### TODO
* yeet the porting lib
* add compat for fancy enchant descriptions mod that aof has

## 1.2.4
* Add a warning if easymagic/easyanvils is installed
* Fix End remastered cryptic eye not being obtainable 
* Fix spell engine enchants showing up on items they shouldn't

## 1.2.3
* Fix Warden Tendril drops

## 1.2.2
* Update to latest ZenithAttributes
* Fix UI crashes

## 1.2.1
* Fix mixin inject error

## 1.2.0
* Update to be in line with Apotheosis 7.3.5 and 7.4.0 
  * Big update, read the whole patch notes [here](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.20/changelog.md)
    * Added the Augmenting table, allowing for re-rolling and upgrading affixes
    * Gui updates to Salvaging, Reforging, Gem Cutting, and the Enchanting Library
    * New potion charm texture
  * Added a couple events that were not implemented

## 1.1.10
* I'm back from a hiatus, apologies it took so long to get this out!
* Hopefully fix bosses not spawning
* Fix spawner's not dropping with silk touch when the spawner module is enabled

## 1.1.9
* Bring back tool action support, should fix issues where Hephaestus tools are incorrectly categorized
* Hopefully fix unknown crash with biome modifiers
* Fix free reforging

## 1.1.8
* Temporarily remove spectrum compat, will readd at a later time
* Fix the mod breaking on newer fapi versions

## 1.1.7
* Update dependencies
* Change how the all stats bonus gem modifier works to be in line with current apoth

## 1.1.6
* Fix for potion crafting not being disabled
* Temp fix for bosses not spawning (Mod incompat with arch)
* Temp fix for item frames with items not spawing (Mod incompat with YUNGS Api)

## 1.1.5
* Fix broken spawner mixin crashing the game when the spawner module is disabled
* Missing enchant no filter (Maikotui)

## 1.1.4
* Compat for clean tooltips
* Slight mixin refactor

## 1.1.3
* Fix mobs not spawning

## 1.1.2
* Move client only accessor to client mixin module
* Fix broken affixes

## 1.1.1
* Move client only accessor to client mixin module
* Fix incorrect version of zenith attributes being required

## 1.1.0
* Update to be in line with Apotheosis [7.3.1](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.20/changelog.md#731)
* Update to latest porting lib. Will break stuff probably!

## 1.0.6
* Properly check if anvil is vanilla and should be enchantable
* Update porting lib loot to fix loot table issues (Full update to latest porting lib coming soon™️)

## 1.0.5
* Fix armor not being repaired with life mending
* Fix enchantments going away when anvil loses durability
* Fix the puzzlelib anvil compat not initiating properly
* Fix curse boss item erroring and not summoning bosses

## 1.0.4
* Fix shift clicking in the salvaging table

## 1.0.3
* Move to transfer api with the following:
  * Salvaging table allows insertion/extraction of salvaged items.
  * Reforging table allows insertion of gem dust.
  * Enchanting table allows insertion/extraction of lapis. THIS WILL DELETE LAPIS CURRENTLY IN THE TABLE!
  * Enchantment Library/Library of Alexandria allows insertion.
* Please report any dupe/deletion bugs resulting from this change to the issue tracker!

## 1.0.2
* Update to be in line with Apotheosis [7.2.2](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.20/changelog.md#722)
* Fix enchanting giving random results
* Fix horrible implementation of advancement triggers
* Fix an exploit with biome makeover and spectrum
* Fix disabled potion charms not being actually disabled

## 1.0.1
* Fix broken recipe for treasure shelf
* Fix Reforging table and Salvaging table not cancelling item's use action when opened
* Fix bug where enchanting seed does not change due to improper casting

## 1.0.0
* First "release" version
* Update to be in line with Apotheosis [7.2.0](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.20/changelog.md#720)
  * This changed how anvils and enchanting took xp to use the optimal xp cost (as if you were spending the exact level)
  * It also added 2 new shelves, one that allows filtering and one that allows
* Finally fix that tag loading bug with enchanting stats in where tags were not supported properly
* Implement EMI support
* Remove Soulslike weaponry compat, no longer needed

## 0.2.4
* Fix crash when adventure module is enabled

## 0.2.3
* Reenable mixin that was causing a crash with Soulslike weaponry, was fixed
* Fix Dragonloot bow and crossbow breaking when it's lootified with the adventure module, and fix it's anvil not supporting the tome of extraction
* Add missing hook for when crossbows are fired
* Redirect goblin trader's enchantment hook to work with the enchantment module's higher max levels
* Add a blacklist to potion charms so they can be disabled from being crafted

## 0.2.2
* Temporarily disable mixin that was causing a crash with Soulslike weaponry
* Increase priority of anvil mixin to override Easy Anvils anvil implementation

## 0.2.1
* Update to be in line with latest Apotheosis version
  * More info [here](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.20/changelog.md#710)
* Fix max villager and loot levels not being increased properly
* Hopefully fix spawners not saving when broken
* Fix inability to launch the game with the potion module disabled and the spawner module enabled
* Fix shearing in dispensers causing a crash

## 0.2.0
* Make anvil repair event fire when in creative mode
* Fix Tome of Extraction not giving item back due to incompatibility with puzzleslib
* Fix modded bookshelves not having eterna
* Fix gem of royal family breaking elytra attribute
* Remove testing mixin that was left in by mistake, causing a crash with newer versions of porting lib
* Fix extraction recipe not working (from upstream)
* Remove random debug code + code cleanup

## 0.1.11
* Fix enchanting library not being openable

## 0.1.10
* Update to fakerlib 0.0.8

## 0.1.9
* Fix books not being enchantable in the table, as well as them generating unenchanted

## 0.1.8
* Fix tomes being unenchantable when spell power is installed
* Fix mobs crashing the game when adventure module is disabled

## 0.1.7
* Fix anvils not dropping with their enchantments
* Add extra spell engine support

## 0.1.6
* Add support for spell engine casting for gems and affixes, has to be implemented by a 3rd party with the api

## 0.1.5
* Fix crafting recipes not working due to incorrect tags

## 0.1.4
* Fix custom menus not opening on servers
* Fixed adventure block entities crashing when opened on servers
* Fix books not being enchantable
* Add WTHIT support, if preferred over Jade

## 0.1.3
* Add attribute config

## 0.1.2
* All affixes are working now (except magical arrow, as it doesn't do anything atm)
* All gems are working now
* Adventure structures are properly disabled when module is disabled

## 0.1.1
* Fix the game not running with the adventure module disabled (structures will still generate, to be fixed)

## 0.1.0
* Adventure module arrives (with no doubt many bugs)!
* Not everything is 100% yet, still need to double-check everything

## 0.0.3
* Added fletching REI support
* Hopefully fix menus not being registered due to a mixin conflict

## 0.0.2
* Added spawner module
* Fix Enchantment Library crashing when placed
* Update dev REI version 
* Fix spell power enchantments applying to things it should not

## 0.0.1
* Initial release