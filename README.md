# PotterPlusAPI-Server

This is the plugin side of [PotterPlusAPI](https://github.com/PotterPlus/PotterPlusAPI) which powers all the custom plugins on the PotterPlus among other things:

* WIP Re-writing Essentials commands we need so we can drop EssentialsX
* Database tracking of user sessions with a MySQL database
* Login history tracking for administrative purposes
* WhoIs command interface for learning more about specific players
* Tracking of player info like role, Hogwarts house, (hopefully soon) year, other progression through LuckPerms

## Commands

* `/ppapi` - PotterPlusAPI central command
* * **Permission** potterplus.admin

Core

* `/feed [players]` - Feed players
* * **Permission** potterplus.command.feed
* `/heal [players]` - Heal players
* * **Permission** potterplus.command.

Main

* `/loginhistory [player]` - Get a player's login history
* * **Permission** potterplus.command.loginhistory
* `/setserverlist <1|2> <message>` - Set the server list MOTD
* * **Permission** potterplus.command.setserverlist
* `/whois <player>` - Learn more about a user
* * **Permission** potterplus.command.whois
