# Cyan's Veinminer

#### _The config file for v1.1.1+ is now simple_veinminer.conf_

### This Is my first actual mod, so some feedback would be appreciated

### As this is my first mod, I'm not 100% sure if I've done everything the most efficient way possible, but I did my best

# Preamble

I recently started playing with a few mods on 1.18.1

It was pretty fun but I got way too used with veinmining on other modpacks

So, I looked for a veinminer for 1.18.1 fabric and... Nothing (Well there's [VeinMiner4Bukkit](https://www.curseforge.com/minecraft/mc-mods/veinminer4bukkit) and [Vein Mining (Fabric)](https://www.curseforge.com/minecraft/mc-mods/vein-mining-fabric) but neither do what I needed)

So, taking a page from Thanos' book, I did it myself

![A meme showing no results for "veinminer" in fabric 1.18.1 ontop and Thanos saying "Fine, I'll do it myself", but with my profile picture (A white girl with long brown curly hair and brown eyes, a golden crown, blue earrings, a pink jumper, a flower crown and glasses) edited in place of his face below](https://github.com/PrincessCyanMarine/Simple-Veinminer/blob/main/assets/fine_ill_do_it_myself.png?raw=true "Fine I'll do it myself")

# Enchantments and multiplayer

I haven't tested any modded enchantments (yet), but the vanilla ones work correctly

From what I've tested, it works fine on multiplayer

But admittedly, I play basically just singleplayer, so if there are multiplayer only issues, I'll mostly likely never find them on my on

So, if you find anything wrong, please do [report it](https://github.com/PrincessCyanMarine/Simple-Veinminer/issues)

# Config

The mod works out of the box but it has some config options you may find useful

I suggest messing around with them a bit to find what fits you best

The easiest way to do that is by using [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) and [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config), but both are optional

# Found a bug? Has a suggestion? Any complaints?

Please leave a comment on [curseforge](https://www.curseforge.com/minecraft/mc-mods/simple-veinminer) or [create an issue](https://github.com/PrincessCyanMarine/Simple-Veinminer/issues) here on Github

# Modpacks

Feel free to add this to as many modpacks as you would like

# Changelog

## [v1.2.0]

### Added

- Config to make the key toggle veinmining on and off
- Synchronization between the client's config and the server config (fixing the disparity between the outline and what was actually broken when on servers)
- Config to only allow veinmining when using the right tool for that block
- Config to hide restriction messages

### Changed

- If the tool breaks while veinmining, the veinmining stops
- Blocks that wouldn't drop anything when broken normally, won't drop when veinmining (Eg.: Mining snow without a shovel or stone without a pickaxe)
- Version requirements for minecraft and fabric, mod should now work for any 1.18.x version currently released (Although only 1.18.1 is _officially_ supported)

[Full changelog](https://github.com/PrincessCyanMarine/Simple-Veinminer/blob/main/CHANGELOG.md)
