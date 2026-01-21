## v1.8.2.3
* made the respawn dimension getter async, hopefully preventing a crash on folia [Paper]

## v1.8.2.2
* fixed jitteriness of the speed module [Paper, Spigot]

## v1.8.2.1
* added the DirectionHUD prefix to the language file

# v1.8.2.0 The Config Update
Added 2 new config options!
\
Hud module toggles, and a toggle for allowing anyone to add and edit global destinations.

* added a new config group `global` under `destination`
  * added a new toggle `public-editing` which allows anyone to add and edit global destinations
* added a new config group `enabled-modules` under `hud`
  * each module under this group is a toggle to enable or disable that module globally
* fixed an issue where the HUD module edit UI would not open when all modules were disabled
* updated translations from Crowdin