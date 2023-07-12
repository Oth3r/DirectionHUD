# DirectionHUD (FABRIC)
[![github](https://img.shields.io/github/v/release/oth3r/DirectionHUD-F?color=blueviolet&logo=github)](https://github.com/Oth3r/DirectionHUD-F/releases)  [![discord](https://dcbadge.vercel.app/api/server/Mec6yNQ9B7?style=flat)](https://discord.gg/Mec6yNQ9B7) [![modrinth](https://img.shields.io/modrinth/dt/directionhud?label=Modrinth&logo=modrinth)](https://modrinth.com/mod/directionhud) [![curseforge](https://cf.way2muchnoise.eu/843483.svg)](https://www.curseforge.com/minecraft/mc-mods/directionhud-fabric)
\
DirectionHUD is a fully customizable Minecraft Fabric mod that enhances the vanilla navigation experience.
With the mod, you can save coordinates, track other players, see death locations, all with full customization with the **/hud** and **/destination** commands.
\
![overview](https://github.com/Oth3r/DirectionHUD/blob/master/media/directionhud%20overview.gif)
\
***Where does the mod work?*** - DirectionHUD both works on client and server, but for now the client side only supports single player & DirectionHUD servers. As with anything, that can change with time.
\
***How do I set custom destinations?*** - You can use **/dest set <XYZ>** to set a destination, but the main commands for the mod are **/hud** and **/destination**, if you are stuck try one of them out!
\
***Why is it coded like a plugin?*** - DirectionHUD is a port of one of my old private plugins, currently trying to add new features to stray away from the plugin feel.
## Features
Press **H** to toggle the hud.
### HUD Customization
Ever wanted everything to be RGB?
\
Toggle and sort HUD modules to your liking, change the colors and style.
\
![hud customization](https://github.com/Oth3r/DirectionHUD/blob/master/media/hud%20customization.gif)

### Saving Coordinates
Tired of writing down base locations?
\
Save, set, convert, and edit coordinates easily using the chat UI.
\
![dest saving](https://github.com/Oth3r/DirectionHUD/blob/master/media/dest%20saving.gif)

### Tracking
Having trouble ~~hunting down~~ keeping up with your friends?
\
Send a tracking request and start your adventures!
\
![dest tracking](https://github.com/Oth3r/DirectionHUD/blob/master/media/tracking.gif)

### Death Saving
*Which cave did i die in...*
\
See your last deaths in each dimension.
\
![lastdeath](https://github.com/Oth3r/DirectionHUD/blob/master/media/lastdeath.gif)

### Destination Customization
MORE CUSTOMIZATION
\
Toggle destination settings, like auto clearing and particles.
\
![adaptive dest](https://github.com/Oth3r/DirectionHUD/blob/master/media/adaptive%20dest.gif)
\
![dest customization](https://github.com/Oth3r/DirectionHUD/blob/master/media/dest%20customization.gif)

### Config Settings
Change default HUD configurations and settings using the **config file** when running on server, or use **[ModMenu](https://modrinth.com/mod/modmenu)** & **[YetAnotherConfigLib](https://modrinth.com/mod/yacl)** when running on client.
\
**/directionhud defaults** also works on the client - Set defaults to your current settings with one click!

## Future Goals
* More UI options
  * Custom client UI: Straying away from the plugin feel for client users.
  * Server inventory UI: Chat scrolling too fast? Want that classic inventory UI feel? 
