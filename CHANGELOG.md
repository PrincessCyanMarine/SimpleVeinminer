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