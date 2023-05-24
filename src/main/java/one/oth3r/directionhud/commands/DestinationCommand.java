package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
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
        int pos = args.length+1;
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        if (pos == 1) {
            if (config.deathsaving && PlayerData.get.dest.setting.lastdeath(player)) suggester.add("lastdeath");
            if (config.DESTSaving) {
                suggester.add("add");
                suggester.add("saved");
            }
            suggester.add("set");
            suggester.add("clear");
            suggester.add("settings");
            if (Destination.showSend(player)) suggester.add("send");
            if (Destination.showTracking(player)) suggester.add("track");
            return suggester;
        }
        if (pos > args.length) {
            return suggester;
        }
        //SAVED
        if (args[1].equalsIgnoreCase("saved")) {
            return Destination.commandSuggester.savedCMD(player,pos-2,Utl.trimStart(args,2));
        }
        //ADD
        if (args[1].equalsIgnoreCase("add")) {
            return Destination.commandSuggester.addCMD(player,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("settings")) {
            return Destination.commandSuggester.settingsCMD(pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("set")) {
            return Destination.commandSuggester.setCMD(player,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("send")) {
            return Destination.commandSuggester.sendCMD(player,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("track")) {
            return Destination.commandSuggester.trackCMD(player,pos-2);
        }
        return new ArrayList<>();
    }
}