# PotterPlusAPI-Server

[![Build Status](https://travis-ci.com/PotterPlus/PotterPlusAPI-Server.svg?token=TSz94qoPczfF4z3iJvmq&branch=main)](https://travis-ci.com/PotterPlus/PotterPlusAPI-Server)

This is the plugin side of [PotterPlusAPI](https://github.com/PotterPlus/PotterPlusAPI) which powers all the custom plugins on the PotterPlus among other things:

* WIP Re-writing Essentials commands we need, so that we can drop EssentialsX
* Database tracking of user sessions with a MySQL database
* Login history tracking for administrative purposes
* WhoIs command interface for learning more about specific players
* Tracking of player info like role, Hogwarts house, (hopefully soon) year, other progression through LuckPerms

## Commands

* `/ppapi` - PotterPlusAPI central command
* * **Permission** potterplus.admin

Essential

* `/clearpots [players]` - Clear potion effects
* * **Permission** potterplus.command.clearpotioneffects
* `/feed [players]` - Feed players
* * **Permission** potterplus.command.feed
* `/gm <gamemode> [player]` - Set gamemode
* * **Permission** potterplus.command.gamemode
* `/heal [players]` - Heal players
* * **Permission** potterplus.command.heal
* `/kill [players]` - Kill players
* * **Permission** potterplus.command.kill
* `/potion <type> [amp] [duration]` - Apply potion effects
* * **Permission** potterplus.command.potion

Main

* `/loginhistory [player]` - Get a player's login history
* * **Permission** potterplus.command.loginhistory
* `/me` - View your character menu
* `/setserverlist <1|2> <message>` - Set the server list MOTD
* * **Permission** potterplus.command.setserverlist
* `/where` - Gets your current location
* * **Permission** potterplus.command.setserverlist
* `/whois <player>` - Learn more about a user
* * **Permission** potterplus.command.whois


## TODO

* Help command replacer
* Plugins command replacer