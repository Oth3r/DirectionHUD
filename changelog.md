# v1.4.0 - QOL & BossBar Support
<iframe width="1043" height="587" src="https://www.youtube.com/embed/CU6PTj8hg_I" title="FINAL" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

Please submit bugs in the GitHub! You can also submit in the discord server if GitHub is confusing.

## New Features
### Color picker
 * can now edit colors using the HSB standard
 * new color presets tab
   * save custom color presets
     * presets that are not white will show up in tab complete for filling out color
   * new default colors, with 3 shades to each color
 * destination particles and saved destinations now work with the color screen
### BossBar support
 * change color to very minecraft bar color
 * progress bar can be used to track distance from the destination
   * change the countdown distance in the settings, 0 = auto
### Other Additions
 * new hud settings page
 * '/dest send' now works with sending color
 * '/dest send' now sends your location if no arguments are entered
 * new tracking module setting to switch between pointing towards the tracked player & destination
 * new destination tracking setting for bypassing the tracking request system
## Fixes & Tweaks
 * HUD will now not display when all modules are off but HUD is on
 * Settings screens now have an 'X' button next to each option for individual resets
 * '/hud edit' is now '/hud modules'
 * config is 100% redone (modmenu & config file)
   * hud order in config is now a list, not a string
 * DirectionHUD LangReader is now using Gson
 * fixed error text when making a new destination with a duplicate name 
 * fixed message for setting the tracker, its no longer using the destination
 * fixed '/directionhud' command not working when hud editing config was turned off
 * more info in settings and other areas
 * optimizations
 * & more