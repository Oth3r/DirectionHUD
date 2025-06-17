## v1.8.1.0 Modular Modules pt.2
Modular Modules? What about MORE modularity? \
This update adds more to the base update that came before, with the backend changes that add dynamic module settings and display settings.

### Modules
#### New light level module!
This module includes an option to choose between viewing the light level at the player's target block or below the player with the light_target setting.
The other setting for this module is the light_display setting. It can be used to choose between displaying skylight, block light, or both.

#### Module Settings
Changed module setting names to be more unique. This change is made for the new settings system. \
Setting that are on / off now can be toggled with true / false

#### New HUD Module UI
With the new Module Edit UI introduced in 1.8, there was going to be an issue when more modules got added.
To fix this, disabled modules and enabled modules are now split! \
Disabled modules now are orderless, and their order will automatically be the last in the module order when enabled / re-enabled.

#### Module Text File
The module-text.json file is now on version 1.1, and has gotten a structure change. 
Every module has been moved from the root to the "modules" node on the root. 
Each module now has a "displays" and an "assets" node to better distinguish what is what. \
The new system doesn't populate every module entry on load, and only loads what is entered to the file.
To help, a new toggle, "load-missing" was added for loading missing entries to the file.

#### Backend rework
Adding new modules was always a pain because of the way I first wrote them a good 4 years ago. 
Everything was hardcoded, down to how they were displayed. 
DirectionHUD 1.8 helped lighten the issue with the module text system, and a new way of registering modules.
That wasn't enough though, and now, with 1.8.1, *most* everything to do with modules are easier with a registration system!

### Changelog:
* added light module, defaults to off
  * added light_target setting, defaults to eye
  * added light_display setting, defaults to block
* added show-name setting to destination module
* changed the module setting names to be more unique
  * angle
    * display -> angle_display
  * tracking
    * hybrid -> tracking_hybrid
    * target -> tracking_target
    * display-type -> tracking_display
    * show-elevation -> tracking_show-elevation
  * time
    * 24hr-clock -> time_24hr
  * speed
    * 2d-calculation -> speed_2d-calculation
    * display-pattern -> speed_display-pattern
  * coordinates
    * xyz-display -> coordinates_xyz-display
* tweaked the module edit UI
  * added a new section for disabled modules
  * hud edit UI now only shows enabled modules
* removed hud order from disabled modules
* added support for entering true / false for toggleable settings
* added module display and setting registration system
* added module reset confirmation before actually resetting
* reverted language file capitalization change (en_US -> en_us)
* added support for overriding the language file via the configs
  * put the custom language file in the `config/directionhud/lang/` folder
* bumped packet version
* more backend changes
* module-text version 1.01 -> 1.1
* playerdata & default-playerdata version 2.1 -> 2.3
* config version 1.61 -> 1.7
* other bug fixes and optimizations
