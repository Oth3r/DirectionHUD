# v1.5
### Rewrites & Refreshes
* rewrote client-server networking
  * DirectionHUD clients are now compatible with Spigot servers
  * when on the client, ActionBars are now built on the client
  * non DirectionHUD ActionBars now show up for a short period of time when on client
* destination logic rewrite
  * new edit destination UI
  * all `/dest saved` commands are in command suggestions
  * `/dest remove` moved to `/dest saved delete`
* LastDeath rework
  * removed the ability of clearing deaths
  * now saves last `x` deaths (edit in config) instead of last death per dimension
  * new page system for when there are more saved deaths than what can be shown
* HUD Modules rewrite
  * new modules UI
  * uses the new page system, for more module support
  * turning off a module doesn't move it to the back of the list anymore
  * all module commands are now in command suggester
* Track command rewrite
  * track commands are now split up and easier to understand `/dest track (set|clear*|cancel*|accept*|deny*)`
  * all track commands now show up in the suggester
### New Additions
* Global Destinations
  * `/dest saved global [add|edit|delete]`
  * new `global-destinations` config option, default `false`
  * new `global-dest.json` (with pretty json enabled!!)
  * enables a new tab in the saved destinations UI that all players can see
  * only people with the right permissions can edit the global destinations
* new Inbox System
  * `/dhud inbox`
  * Pending track requests, track requests, and received destinations will now show up in the inbox
  * central place for all social command interactions
* sending destinations and track requests now have a cooldown to combat command spamming
### Config Changes
* updated to v1.4
* new `social-cooldown` config option, default `10` seconds, `0` to disable
* new `global-destinations` config option, default `false`
* moved `hud.enabled` to `hud.settings.state`
* added `particle-loop` config option to change particle loop speed
* renamed `hud-refresh` config option to `hud-loop`
* lastdeath config~!!!!!!
### Fixes and Changes
* Renamed `/dirhud` command to `/dhud`
  * the `/dhud` command now works to execute every command in DirectionHUD (eg. `/dhud dest saved`)
  * `/dhud reload` now fully reloads all players
  * removed `/directionhud defaults` command *(might come back later if requested, but was a pain to keep it working, needs to be heavily redone)*
* moved hud toggle to the settings page
  * removed toggle button from HUD menu, the command still is functional
* PlayerData v1.6
  * The PlayerData JSON is now formatted in a readable way
* fixed players not unloading fully on server shutdown
* fixed tracking module pointing to player target when the target is in another dimension and AutoConvert is off
* fixed `tracking resumed` message not showing up when turning on AutoConvert
