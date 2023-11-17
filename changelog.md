### TODO
* EMI support
* Add ability to enchant mining arrows
* Fix salvaging non single stacks of items not giving the proper amount of items in return


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