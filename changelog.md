# v1.8.2.0 The Config Update
Added 2 new config options!
\
Hud module toggles, and a toggle for allowing anyone to add and edit global destinations.

* added a new config group `global` under `destination`
  * added a new toggle `public-editing` which allows anyone to add and edit global destinations
* added a new config group `enabled-modules` under `hud`
  * each module under this group is a toggle to enable or disable that module globally
* fixed an issue where the HUD module edit UI would not open when all modules were disabled