## v1.8.0 - Custom Module Text!

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
