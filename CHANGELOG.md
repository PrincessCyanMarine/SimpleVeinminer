# Changelog
<!-- All notable changes to this project (post version 5.3.0) will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). -->

<!-- 
## [Unreleased]

## [0.0.0] - year-month-day
### Added

### Changed

### Removed
 -->

## [v1.4.2]
### Added
- Re-added cloth config integration
- 
## [v1.4.1]
### Added
- More commands
### Changed
- Updated for minecraft 1.20+
- Won't drop xp when mining with an inadequate tool (e.g. using a wooden pickaxe to mine diamond ore)
### Removed
- Cloth config integration (for now)


## [v1.4.0] (BETA)
### Added
- Added more highlight options
- Added "place in inventory" config option
- Added commands to change config options
### Removed
- Shapes
### Changed
- Updated for minecraft 1.19.4
- "Outline" changed to "Highlight"

## [v1.3.2]
- Added the hammer veinmining shape
- Added "Exhaust" config option


## [v1.3.1]
- Mining progress is also showed on blocks being veinmined (can be deactivated, only shows to the player who's veinmining said blocks)
- Added config option to make the block limit variable depending on the tool used (if turned on, "maxBlocks" is divided by max(1, 6 - (toolMiningLevel + 1))) (shears are counted as having the same mining level as IRON)
- Added Gamerule to turn serverside veinmining on (doServerSideVeinmining). When on, mining while shifted will veinmine, whether the player has the mod or not. If the player has the mod and serverside veinmining is on, shifting will show outlines and mining progress (if they are turned on)


## [v1.3.0]
### Changed
- Updated for minecraft 1.19

## [v1.2.1]
### Changed
- Updated to work on 1.18.2
- Added #c:logs tag
- Changed #c:ores tag to include some modded ores

## [v1.2.0]
### Added
- Config to make the key toggle veinmining on and off
- Synchronization between the client's config and the server config (fixing the disparity between the outline and what was actually broken when on servers)
- Config to only allow veinmining when using the right tool for that block
- Config to hide restriction messages
### Changed
- If the tool breaks while veinmining, the veinmining stops
- Blocks that wouldn't drop anything when broken normally, won't drop when veinmining (Eg.: Mining snow without a shovel or stone without a pickaxe)
- Version requirements for minecraft and fabric, mod should now work for any 1.18.x version currently released (Although only 1.18.1 is *officially* supported)

## [v1.1.1]
### Added
- An outline when looking at a block whilst holding the veinmine key (said outline can be turned off on the config)
- Config to change the outline color
- Option to only allow veinmining with a tool
- Blacklist and whitelist
### Changed
- The formula for exhaustion
