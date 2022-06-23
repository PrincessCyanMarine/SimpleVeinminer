# Cyan's Veinminer

#### _The config file for v1.1.1+ is now simple_veinminer.conf_

### This Is my first actual mod, so some feedback would be appreciated

### As this is my first mod, I'm not 100% sure if I've done everything the most efficient way possible, but I did my best

# Preamble

I recently started playing with a few mods on 1.18.1

It was pretty fun but I got way too used with veinmining on other modpacks

So, I looked for a veinminer for 1.18.1 fabric and... Nothing (Well there's [VeinMiner4Bukkit](https://www.curseforge.com/minecraft/mc-mods/veinminer4bukkit) and [Vein Mining (Fabric)](https://www.curseforge.com/minecraft/mc-mods/vein-mining-fabric) but neither do what I needed)

So, taking a page from Thanos' book, I did it myself

![A meme showing no results for "veinminer" in fabric 1.18.1 ontop and Thanos saying "Fine, I'll do it myself", but with my profile picture (A white girl with long brown curly hair and brown eyes, a golden crown, blue earrings, a pink jumper, a flower crown and glasses) edited in place of his face below](https://github.com/PrincessCyanMarine/SimpleVeinminer/blob/1.19-v1.3.0/assets/fine_ill_do_it_myself.png?raw=true "Fine I'll do it myself")

# Enchantments and multiplayer

I haven't tested any modded enchantments (yet), but the vanilla ones work correctly

From what I've tested, it works fine on multiplayer

But admittedly, I play basically just singleplayer, so if there are multiplayer only issues, I'll mostly likely never find them on my on

The mod needs to be installed on the server and clients for it to work properly

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

## [v1.3.1]
- Mining progress is also showed on blocks being veinmined (can be deactivated, only shows to the player who's veinmining said blocks)
- Added config option to make the block limit variable depending on the tool used (if turned on, "maxBlocks" is divided by max(1, 6 - (toolMiningLevel + 1))) (shears are counted as having the same mining level as IRON)
- Added Gamerule to turn serverside veinmining on (doServerSideVeinmining). When on, mining while shifted will veinmine, whether the player has the mod or not. If the player has the mod and serverside veinmining is on, shifting will show outlines and mining progress (if they are turned on)

[Full changelog](https://github.com/PrincessCyanMarine/Simple-Veinminer/blob/main/CHANGELOG.md)


# TODO? (No promises tho, college is tough and time is limited)
- ~~Config to make different tier tools have different max blocks~~
- Optionally serverside only (as suggested in this [comment](https://www.curseforge.com/minecraft/mc-mods/simple-veinminer#c22))
- Shapes (as suggested in this [comment](https://www.curseforge.com/minecraft/mc-mods/simple-veinminer#c26))
