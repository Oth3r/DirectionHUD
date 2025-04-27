## v1.8.0.6
* fixed HUD color `reset` button not working in the color edit screen 
* synced localization files with Crowdin - thanks to all the contributors!
  * German (de_De) and Slovak (sk_SK) are now at 100% translated!

## v1.8.0.5
* removed debug console printing on client (Fabric) (1.21.5)
* fixed crash when tracking a target with no y level with elevation enabled on the tracking module

## v1.8.0.4
* fixed actionbar not displaying as fast on client (Fabric) (1.21.5)

## v1.8.0.3
port to spigot and paper, also now with support for folia!

* spigot and paper now have separate jar files
* both versions are now bumped up to v1.8
* the paper version now supports folia
* all versions are now available on the main [DirectionHUD Modrinth page](https://modrinth.com/mod/directionhud).

### Changelog:
* new icon
* bumped hud packet version to `v1.3` to support new plugin versions of DirectionHUD
* fixed `/hud` color button hover color changing when hovering on different parts of the button

## v1.8.0.2
* fixed toggling BossBar while on client side causing a crash

## v1.8.0.1
* fixed destination module `name` and `name_xz` having the primary and secondary flipped inside of `module-text.json`
* changed module name displays to be lowercase
* fixed missing language files
* edited clarifications to some hud module messages
* config file for languages now shows the percent of translated text
* language files with missing translations will default to english on servers

## v1.8.0.0 - Custom Module Text!

### Custom Module Text
Added a new json file, `module-text.json` to customize each module's display settings.
\
Customize each entry using color and formatting codes, and use `%s` to substitute for the data being used for the module.
\
Read more about this on the new [DirectionHUD Docs](https://oth3r.github.io/DirectionHUD/module-text-json.html)!

### New Module Edit UI
A new GUI for editing a module!

![module edit gui](https://oth3r.github.io/images/mods/directionhud/1.8/module_edit.png)

### More Module Settings!
* coordinates module `xz` mode
* tracking module `elevation` toggle
* flipped speed module speed calculation setting

## Changelog:

* added hud module edit chat UI
* added hud module chat UI
* added coordinates module `xz` - `xyz` toggle
* added tracking module `elevation` toggle
* added fuzzy sort filtering to command suggestions - thanks to greener.ca for the help!
* fixed command suggester breaking when using the `execute run` command
* fixed destination autoconvert not working
* flipped speed module setting from 3d-calculation to 2d-calculation
* The third argument in the `/hud color` command always displays even when it shouldn't
* fixed `/hud color [primary, secondary]` command to display the color edit GUI
* fixed the `/hud color` command's third command argument always displaying
* migrated all files to new system
* major backend changes & optimization
* fixes to legacy file updaters
* updated the module system in the playerdata file to be modular
