# Cyan's Veinminer

## This is a ***veinmining*** mod
### Veinmining mines veins of a same block. For mining 3x3s, look for a hammer/excavator mod
### To activate veinmining, use the keybinding. Keybindings can be changed in the controls menu
### Please read this page before asking questions

---
#### _The config file for v1.1.1+ is now simple_veinminer.conf_

### This Is my first actual mod, so some feedback would be appreciated

### As this is my first mod, I'm not 100% sure if I've done everything the most efficient way possible, but I did my best

# Preamble

I recently started playing with a few mods on 1.18.1

It was pretty fun but I got way too used with veinmining on other modpacks

So, I looked for a veinminer for 1.18.1 fabric and... Nothing (Well there's [VeinMiner4Bukkit](https://www.curseforge.com/minecraft/mc-mods/veinminer4bukkit) and [Vein Mining (Fabric)](https://www.curseforge.com/minecraft/mc-mods/vein-mining-fabric) but neither do what I needed)

So, taking a page from Thanos' book, I did it myself

![A meme showing no results for "veinminer" in fabric 1.18.1 ontop and Thanos saying "Fine, I'll do it myself", but with my profile picture (A white girl with long brown curly hair and brown eyes, a golden crown, blue earrings, a pink jumper, a flower crown and glasses) edited in place of his face below](https://github.com/PrincessCyanMarine/SimpleVeinminer/blob/1.19/assets/fine_ill_do_it_myself.png?raw=true "Fine I'll do it myself")

# Enchantments and multiplayer

I haven't tested any modded enchantments (yet), but the vanilla ones work correctly

From what I've tested, it works fine on multiplayer

But admittedly, I play basically just singleplayer, so if there are multiplayer only issues, I'll mostly likely never find them on my on

The mod needs to be installed on the server and is recommended on the client

So, if you find anything wrong, please do [report it](https://github.com/PrincessCyanMarine/Simple-Veinminer/issues)

# Config

The mod works out of the box, but it has some config options you may find useful

I suggest messing around with them a bit to find what fits you best

The easiest way to do that is by using [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) and [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config), but both are optional

# Commands
The config options can also be changed using /veinmining or /veinminingclient

# Highlighting
There are config options to highlight what blocks will be mined

When using the "outline" mode, I recommend turning on "highlight only exposed". As the outline of non-exposed blocks can't be seen anyway

Beware of the "highlight only exposed" option, as it can lag your game when highlighting a large number of blocks depending on your how powerful your computer is (Having "max blocks" set too high would lag (or even crash) your game when mining anyway. So be careful with that)

# ~~Shapes (BETA)~~ Removed
~~Using the /veinmining command, you can choose a shape (only hammer or regular as of now) to veinmine
Shapes need to be turned on in the config as they are able to bypass the block limit~~


# Found a bug? Has a suggestion? Any complaints?

Please leave a comment on [curseforge](https://www.curseforge.com/minecraft/mc-mods/simple-veinminer) or [create an issue](https://github.com/PrincessCyanMarine/Simple-Veinminer/issues) here on Github

# Modpacks

Feel free to add this to as many modpacks as you would like

# Other stuff
If you are having trouble with "restrictions" configs. Make sure "Ignore restrictions when on creative mode" is set to false

# Changelog

## [v1.4.0] Comming soon
### Added
- Added more highlight options
- Added "place in inventory" config option
### Removed
- Shapes
### Changed
- Updated for minecraft 1.19.4
- "Outline" changed to "Highlight"

[Full changelog](https://github.com/PrincessCyanMarine/Simple-Veinminer/blob/1.19/CHANGELOG.md)


# TODO? (No promises tho, college is tough and time is limited)
- ~~Config to make different tier tools have different max blocks~~
- ~~Optionally serverside only (as suggested in this [comment](https://www.curseforge.com/minecraft/mc-mods/simple-veinminer#c22))~~
- ~~Shapes (as suggested in this [comment](https://www.curseforge.com/minecraft/mc-mods/simple-veinminer#c26))~~ **(*Removed*)**