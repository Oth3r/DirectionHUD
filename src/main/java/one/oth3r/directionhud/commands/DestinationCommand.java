package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class DestinationCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (!player.hasPermission("directionhud.destination")) return true;
        if (args.length == 0) {
            Destination.UI(player);
            return true;
        }
        //SET
        if (args[0].equalsIgnoreCase("set")) {
            return Destination.commandExecutor.setCMD(player, Utl.trimStart(args,1));
        }
        //CLEAR
        if (args[0].equalsIgnoreCase("clear")) {
            Destination.clear(player, null);
            return true;
        }
        //SAVED
        if (args[0].equalsIgnoreCase("saved")) {
            return Destination.commandExecutor.savedCMD(player,Utl.trimStart(args,1));
        }
        //ADD
        if (args[0].equalsIgnoreCase("add")) {
            return Destination.commandExecutor.addCMD(player,Utl.trimStart(args,1));
        }
        //REMOVE (HIDDEN)
        if (args[0].equalsIgnoreCase("remove")) {
            return Destination.commandExecutor.removeCMD(player,Utl.trimStart(args,1));
        }
        //LASTDEATH
        if (args[0].equalsIgnoreCase("lastdeath")) {
            return Destination.commandExecutor.lastdeathCMD(player,Utl.trimStart(args,1));
        }
        //SETTINGS
        if (args[0].equalsIgnoreCase("settings")) {
            return Destination.commandExecutor.settingsCMD(player,Utl.trimStart(args,1));
        }
        //SEND
        if (args[0].equalsIgnoreCase("send")) {
            return Destination.commandExecutor.sendCMD(player,Utl.trimStart(args,1));
        }
        //TRACK
        if (args[0].equalsIgnoreCase("track")) {
            return Destination.commandExecutor.trackCMD(player,Utl.trimStart(args,1));
        }
        player.spigot().sendMessage(CUtl.error(CUtl.lang("error.command")));
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        int pos = args.length;
        if (!(sender instanceof Player player)) {
            return suggester;
        }
        if (!player.hasPermission("directionhud.destination")) return suggester;
        if (pos == 1) {
            suggester.addAll(Destination.commandSuggester.base(player));
        }
        if (args.length >= 1) {
            //SAVED
            if (args[0].equalsIgnoreCase("saved")) {
                suggester.addAll(Destination.commandSuggester.savedCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            //ADD
            if (args[0].equalsIgnoreCase("add")) {
                suggester.addAll(Destination.commandSuggester.addCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("settings")) {
                suggester.addAll(Destination.commandSuggester.settingsCMD(pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("set")) {
                suggester.addAll(Destination.commandSuggester.setCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("send")) {
                suggester.addAll(Destination.commandSuggester.sendCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("track")) {
                suggester.addAll(Destination.commandSuggester.trackCMD(player,pos-2));
            }
        }
        return Utl.formatSuggestions(suggester,args);
    }
}